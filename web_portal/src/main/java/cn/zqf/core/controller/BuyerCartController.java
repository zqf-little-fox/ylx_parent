package cn.zqf.core.controller;

import cn.zqf.core.entity.BuyerCart;
import cn.zqf.core.entity.Result;
import cn.zqf.core.service.CartService;
import cn.zqf.core.util.Constants;
import cn.zqf.core.util.CookieUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class BuyerCartController {
    @Reference
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    //添加商品到购物车 @CrossOrigin注解相当于设置了响应头信息
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:8085",allowCredentials = "true")
    //                                    库存ID    数量
    public Result addGoodsToCartList(Long itemId,Integer num){
        try {
            //1. 获取当前登录的用户名称
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            //2. 获取购物车列表
            List<BuyerCart> cartList = findCartList();
            //3. 将当前商品加入到购物车列表
            cartList = cartService.addItemToCartList(cartList, itemId, num);
            //4. 判断用户是否登录 未登录用户为anonymousUser
            if ("anonymousUser".equals(userName)){
                //   如果未登录 将购物车列表存储在Cookie中
                CookieUtil.setCookie(request, response, Constants.CART_LIST_COOKIE, JSON.toJSONString(cartList), 60*60*24*30, "utf-8");
            }else{
                //   如果已登录 将购物车列表存入redis中
                cartService.setCartListRedis(userName,cartList);
            }
            return new Result(true, "添加成功");
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

    //获取购物车所有的返回数据
    @RequestMapping("/findCartList")
    public List<BuyerCart> findCartList(){
        //1. 获取当前登录的用户名称
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        //2. 从Cookie中获取购物车列表
        String cookieCartListStr = CookieUtil.getCookieValue(request, Constants.CART_LIST_COOKIE, "UTF-8");
        //3. 如果购物车列表JSON串为空
        if (cookieCartListStr == null || "".equals(cookieCartListStr)){
            cookieCartListStr = "[]";
        }
        //4. 将购物车列表转JSON
        List<BuyerCart> cookieCartList = JSON.parseArray(cookieCartListStr, BuyerCart.class);
        //5. 判断用户是否登录
        if ("anonymousUser".equals(userName)){
            return cookieCartList;
        }else{
            //已经登录 从redis中获取数据
            List<BuyerCart> redisCartList = cartService.getCartListFromRedis(userName);
            if (cookieCartList.size() > 0){
                //redis中和Cookie中完全合并成一个对象
                cartService.mergeCookieCartListFromRedis(cookieCartList, redisCartList);
                //删除cookie中的购物车列表
                CookieUtil.deleteCookie(request,response,Constants.CART_LIST_COOKIE);
                //合并购物车添加到redis中
                cartService.setCartListRedis(userName,redisCartList);
            }
            return redisCartList;
        }
    }
}
