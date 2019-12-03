package cn.zqf.core.service.impl;

import cn.zqf.core.dao.item.ItemDao;
import cn.zqf.core.entity.BuyerCart;
import cn.zqf.core.pojo.item.Item;
import cn.zqf.core.pojo.order.OrderItem;
import cn.zqf.core.service.CartService;
import cn.zqf.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<BuyerCart> addItemToCartList(List<BuyerCart> cartList, Long itemId, Integer num) {
        //1. 根据商品ID查询商品的信息
        Item item = itemDao.selectByPrimaryKey(itemId);
        //2. 判断商品是否存在 抛异常
        if (item == null){
            throw new RuntimeException("该商品不存在");
        }
        //3. 判断该商品是否为1 已经审核状态 状态不对抛异常
        if (!"1".equals(item.getStatus())){
            throw new RuntimeException("该商品状态无效");
        }
        //4. 获取商家的ID
        String sellerId = item.getSellerId();
        //5. 根据商家ID 查询购物车列表中是否存在该商家的购物车
        BuyerCart cart = searchCartBySellerId(cartList,sellerId);
        //6. 判断如果该购物车列表中不存在该商家的购物车
        if (cart == null){
            //6.1 新建购物车对象
            cart = new BuyerCart();
            //6.2 创建购物车对象卖家ID
            cart.setSellerId(sellerId);
            //6.3 创建购物车对象卖家名称
            cart.setSellerName(item.getSeller());
            //6.4 创建购物项集合
            List<OrderItem> orderItemList = new ArrayList<>();
            //6.5 创建购物项(购物车明细)
            OrderItem orderItem = createOrderItem(item, num);
            //6.6 将购物项加入到购物项集合中
            orderItemList.add(orderItem);
            //6.7 将购物项集合加入到购物车中
            cart.setOrderItemList(orderItemList);
            //6.8 将新建的购物车对象添加到购物车列表中
            cartList.add(cart);
        }else{//否则如果购物车列表中存在商家的购物车
            List<OrderItem> orderItemList = cart.getOrderItemList();
            OrderItem orderItem = searchOrderItemByItemId(orderItemList, itemId);
            //6.9 判断购物车明细是否为空
            if (orderItem == null) {
                //6.10 为空 添加新的明细
                orderItem = createOrderItem(item, num);
                orderItemList.add(orderItem);
            }else {
                //6.11 不为空 在原来购物车基础上添加商品的数量 更改金额
                orderItem.setNum(orderItem.getNum() + num);
                //6.12 设置总价
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));
                //6.13 如果购物车明细数量<=0 则删除
                if (orderItem.getNum() <= 0){
                    orderItemList.remove(orderItem);
                }
                //6.14 如果购物车明细表数据为空 则移除
                if (orderItemList.size() <= 0){
                    cartList.remove(cart);
                }
            }
        }
        //7 返回购物车列表对象
        return cartList;
    }

    //根据商家ID查询购物车对象
    private BuyerCart searchCartBySellerId(List<BuyerCart> cartList, String sellerId) {
        if (cartList != null) {
            for (BuyerCart cart : cartList) {
                if (cart.getSellerId().equals(sellerId)) {
                    return cart;
                }
            }
        }
        return null;
    }

    //根据商品明细id查询商品明细
    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItemList, Long itemId) {
        if (orderItemList != null) {
            for (OrderItem orderItem : orderItemList) {
                if (orderItem.getItemId().equals(itemId)) {
                    return orderItem;
                }
            }
        }
        return null;
    }

    //创建购物车明细(购物项)
    private OrderItem createOrderItem(Item item, Integer num) {
        if (num <= 0){
            throw new RuntimeException("购买数量非法");
        }
        OrderItem orderItem = new OrderItem();
        //商品明细id
        orderItem.setGoodsId(item.getGoodsId());
        //商品的id
        orderItem.setItemId(item.getId());
        //购买的数量
        orderItem.setNum(num);
        //商品单价
        orderItem.setPrice(item.getPrice());
        //商品图片
        orderItem.setPicPath(item.getImage());
        //卖家id
        orderItem.setSellerId(item.getSellerId());
        //标题
        orderItem.setTitle(item.getTitle());
        //总价                                item.getPrice().multiply(new BigDecimal(num));
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));
        return orderItem;
    }

    @Override
    public void setCartListRedis(String userName, List<BuyerCart> cartList) {
        redisTemplate.boundHashOps(Constants.CART_LIST_REDIS).put(userName,cartList);
    }

     //从redis中查询购物车
    @Override
    public List<BuyerCart> getCartListFromRedis(String userName) {
        List<BuyerCart> cartList = (List<BuyerCart>) redisTemplate.boundHashOps(Constants.CART_LIST_REDIS).get(userName);
        if (cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    //合并购物车
    @Override
    public List<BuyerCart> mergeCookieCartListFromRedis(List<BuyerCart> cookieCartList, List<BuyerCart> redisCartList) {
        if (cookieCartList != null){
            //遍历购物车集合
            for (BuyerCart cookieCart : cookieCartList) {
                for (OrderItem cookieOrderItem : cookieCart.getOrderItemList()) {
                    //将购物车集合加入到redis购物车集合中
                    redisCartList = addItemToCartList(redisCartList, cookieOrderItem.getItemId(), cookieOrderItem.getNum());
                }
            }
        }
        return redisCartList;
    }

}
