package cn.zqf.core.service.impl;

import cn.zqf.core.dao.good.GoodsDao;
import cn.zqf.core.dao.good.GoodsDescDao;
import cn.zqf.core.dao.item.ItemCatDao;
import cn.zqf.core.dao.item.ItemDao;
import cn.zqf.core.pojo.good.Goods;
import cn.zqf.core.pojo.good.GoodsDesc;
import cn.zqf.core.pojo.item.Item;
import cn.zqf.core.pojo.item.ItemCat;
import cn.zqf.core.pojo.item.ItemQuery;
import cn.zqf.core.service.CmsService;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CmsServiceImpl implements CmsService, ServletContextAware {
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;
    @Autowired
    private ServletContext servletContext;

    @Override
    public Map<String, Object> findGoodsData(Long goodsId) {
        Map<String, Object> resultMap = new HashMap<>();
        //获取商品的数据
        Goods goods = goodsDao.selectByPrimaryKey(goodsId);
        //获取商品的详情数据
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(goodsId);
        //获取商品的库存集合数据
        ItemQuery query = new ItemQuery();
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        List<Item> itemList = itemDao.selectByExample(query);
        //获取商品对应分类数据
        if (goods != null){
            ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
            ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
            ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());
            //封装数据
            resultMap.put("itemCat1",itemCat1);
            resultMap.put("itemCat2",itemCat2);
            resultMap.put("itemCat3",itemCat3);
        }
        //将商品的所有数据封装成Map返回 key--看模板
        resultMap.put("goods",goods);
        resultMap.put("goodsDesc",goodsDesc);
        resultMap.put("itemList",itemList);
        return resultMap;
    }

    @Override
    public void createStaticPage(Long goodsId, Map<String, Object> rootMap) throws Exception {
        //获取模板的初始化对象
        Configuration configuration = freeMarkerConfig.getConfiguration();
        //获取模板对象
        Template template = configuration.getTemplate("item.ftl");
        //创建输出流，指定生成惊天页面的位置和名称
        String path = goodsId+".html";
        //获取绝对路径
        String realPath = getRealPath(path);
        System.out.println("realPath：" + realPath);
        Writer out = new OutputStreamWriter(new FileOutputStream(new File(realPath)), "utf-8");
        //生成
        template.process(rootMap,out);
        //关闭流
        out.close();
    }

    //将相对路径转换为绝对路径
    private String getRealPath(String path){
        String realPath = servletContext.getRealPath(path);
        return realPath;
    }

    @Override
    public void setServletContext(javax.servlet.ServletContext servletContext) {

    }
}




















