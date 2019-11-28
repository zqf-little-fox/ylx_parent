package cn.zqf.core.service.impl;

import cn.zqf.core.dao.good.BrandDao;
import cn.zqf.core.dao.good.GoodsDao;
import cn.zqf.core.dao.good.GoodsDescDao;
import cn.zqf.core.dao.item.ItemCatDao;
import cn.zqf.core.dao.item.ItemDao;
import cn.zqf.core.dao.seller.SellerDao;
import cn.zqf.core.entity.GoodsEntity;
import cn.zqf.core.entity.PageResult;
import cn.zqf.core.pojo.good.Brand;
import cn.zqf.core.pojo.good.Goods;
import cn.zqf.core.pojo.good.GoodsDesc;
import cn.zqf.core.pojo.good.GoodsQuery;
import cn.zqf.core.pojo.item.Item;
import cn.zqf.core.pojo.item.ItemCat;
import cn.zqf.core.pojo.item.ItemQuery;
import cn.zqf.core.pojo.seller.Seller;
import cn.zqf.core.service.GoodsService;
import cn.zqf.core.service.SolrManagerService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {
    //商品的Dao
    @Autowired
    private GoodsDao goodsDao;
    //商品详情的Dao
    @Autowired
    private GoodsDescDao goodsDescDao;
    //库存Dao
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private JmsTemplate jmsTemplate;
    //商品上架使用
    @Autowired
    private ActiveMQTopic topicPageAndSolrDestination;
    //商品下架使用
    @Autowired
    private ActiveMQQueue queueSolrDeleteDestination;

    @Override
    public GoodsEntity findOne(Long id) {
        GoodsEntity goodsEntity = new GoodsEntity();

        Goods goods = goodsDao.selectByPrimaryKey(id);
        goodsEntity.setGoods(goods);

        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        goodsEntity.setGoodsDesc(goodsDesc);

        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(query);
        goodsEntity.setItemList(items);

        return goodsEntity;
    }

    @Override
    public void add(GoodsEntity goodsEntity) {
        //1. 保存商品对象
        goodsEntity.getGoods().setAuditStatus("0");
        goodsDao.insertSelective(goodsEntity.getGoods());

        //2. 保存商品详情对象
        //商品的主键作为商品详情的主键
        Long goodsId = goodsEntity.getGoods().getId();
        goodsEntity.getGoodsDesc().setGoodsId(goodsId);
        goodsDescDao.insertSelective(goodsEntity.getGoodsDesc());

        //3. 保存库存集合对象
        insertItem(goodsEntity,"add");
    }

    @Override
    public PageResult search(Goods goods, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        GoodsQuery query = new GoodsQuery();
        GoodsQuery.Criteria criteria = query.createCriteria();
        criteria.andIsDeleteIsNull();
        if (goods != null){
            if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus())){
                criteria.andAuditStatusEqualTo(goods.getAuditStatus());
            }
            if (goods.getGoodsName() != null && !"".equals(goods.getGoodsName())){
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getSellerId() != null && !"".equals(goods.getSellerId())){
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
        }
        Page<Goods> page = (Page<Goods>) goodsDao.selectByExample(query);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void delete(Long id) {
        Goods goods = goodsDao.selectByPrimaryKey(id);
        goods.setIsDelete("1");
        goodsDao.updateByPrimaryKey(goods);
        //将商品的id作为消息发送给消息服务器
        jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                return textMessage;
            }
        });
    }

    @Override
    public void updateStatus(Long id, String status) {
        //1.根据商品的id 修改商品的状态码
        Goods goods = goodsDao.selectByPrimaryKey(id);
        goods.setAuditStatus(status);
        goodsDao.updateByPrimaryKey(goods);
        //2根据商品的id  修改库存对象的状态码
        Item item = new Item();
        item.setStatus(status);
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        itemDao.updateByExampleSelective(item,query);
        //将商品的id作为消息发送给消息服务器
        /*if ("1".equals(status)){
            jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                    return textMessage;
                }
            });
        }*/
    }

    @Override
    public void update(GoodsEntity goodsEntity) {
        //修改商品对象
        goodsDao.updateByPrimaryKeySelective(goodsEntity.getGoods());
        //修改商品详情对象
        goodsDescDao.updateByPrimaryKeySelective(goodsEntity.getGoodsDesc());
        //删除所有商品库存列表
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsEntity.getGoods().getId());
        List<Item> itemList = itemDao.selectByExample(query);
        Date createName = null;
        for (Item item : itemList) {
            createName = item.getCreateTime();
        }
        itemDao.deleteByExample(query);
        //添加新的库存对象
        insertItem(goodsEntity,createName);

        //商品上架 修改solr中对应的商品数据和详情页面
        if ("1".equals(goodsEntity.getGoods().getIsMarketable())){
            //将商品的id作为消息发送给消息服务器 删除原来solr中对应的数据
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage textMessage = session.createTextMessage(String.valueOf(goodsEntity.getGoods().getId()));
                    return textMessage;
                }
            });
            //将商品的id作为消息发送给消息服务器 新的数据添加到solr中和生成新的详情页面
            jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage textMessage = session.createTextMessage(String.valueOf(goodsEntity.getGoods().getId()));
                    return textMessage;
                }
            });
        }
    }

    @Override
    public void updateIsMarketable(Long id, String isMarketable) {
        Goods goods = goodsDao.selectByPrimaryKey(id);
        goods.setIsMarketable(isMarketable);
        goodsDao.updateByPrimaryKeySelective(goods);
        if ("1".equals(isMarketable)){
            //将商品的id作为消息发送给消息服务器 商品上架
            jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                    return textMessage;
                }
            });
        }else {
            //将商品的id作为消息发送给消息服务器 商品下架
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                    return textMessage;
                }
            });
        }

    }

    public void insertItem(GoodsEntity goodsEntity,Object method){
        if ("1".equals(goodsEntity.getGoods().getIsEnableSpec())){
            //勾选复选框，有库存数据
            if (goodsEntity.getItemList() != null){
                //库存对象
                for (Item item : goodsEntity.getItemList()) {
                    //标题由商品名称+规格组成 供消费者搜索使用
                    String title = goodsEntity.getGoods().getGoodsName();
                    String specJsonString = item.getSpec();
                    //将json串转换成对象
                    Map specMap = JSON.parseObject(specJsonString, Map.class);
                    //获取map中的value集合
                    Collection<String> values = specMap.values();
                    for (String value : values) {
                        //title = title + value;
                        title += " " + value;
                    }
                    item.setTitle(title);
                    //设置库存的对象的属性值
                    setItemValue(goodsEntity,item,method);
                    itemDao.insertSelective(item);
                }
            }
        }else{
            //没有勾选 没有库存 初始化一条
            Item item = new Item();
            item.setPrice(new BigDecimal("666666"));
            //设置库存量
            item.setNum(9999);
            //初始化规格
            item.setSpec("");
            //设置标题
            item.setTitle(goodsEntity.getGoods().getGoodsName());
            //设置库存对象的属性值
            setItemValue(goodsEntity,item,method);
            itemDao.insertSelective(item);
        }
    }

    private Item setItemValue(GoodsEntity goodsEntity,Item item,Object method){
        //商品的id
        item.setGoodsId(goodsEntity.getGoods().getId());
        //创建时间
        if ("add".equals(method)){
            item.setCreateTime(new Date());
        }
        else {
            item.setCreateTime((Date) method);
        }
        //更新时间
        item.setUpdateTime(new Date());
        //库存状态
        //item.setStatus("0");
        //分类的id 库存分类
        item.setCategoryid(goodsEntity.getGoods().getCategory3Id());
        //分类的名称
        ItemCat itemCat = itemCatDao.selectByPrimaryKey(goodsEntity.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        //品牌的名称
        Brand brand = brandDao.selectByPrimaryKey(goodsEntity.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //卖家的名称
        Seller seller = sellerDao.selectByPrimaryKey(goodsEntity.getGoods().getSellerId());
        item.setSeller(seller.getName());
        //式例的图片
        String itemImages = goodsEntity.getGoodsDesc().getItemImages();
        List<Map> maps = JSON.parseArray(itemImages, Map.class);
        if (maps != null && maps.size() > 0){
            String url = String.valueOf(maps.get(0).get("url"));
            item.setImage(url);
        }
        return item;
    }
}
