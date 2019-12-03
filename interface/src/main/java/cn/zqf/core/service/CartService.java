package cn.zqf.core.service;

import cn.zqf.core.entity.BuyerCart;

import java.util.List;

public interface CartService {
    //将商品加入这个人现有的购物车列表中
    List<BuyerCart> addItemToCartList(List<BuyerCart> cartList, Long itemId, Integer num);

    //将购物车添加到redis集合中
    void setCartListRedis(String userName, List<BuyerCart> cartList);

    //从redis中查询购物车列表
    List<BuyerCart> getCartListFromRedis(String userName);

    //合并购物车
    List<BuyerCart> mergeCookieCartListFromRedis(List<BuyerCart> cookieCartList, List<BuyerCart> redisCartList);
}
