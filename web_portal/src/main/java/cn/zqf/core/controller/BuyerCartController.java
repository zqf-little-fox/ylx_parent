package cn.zqf.core.controller;

import cn.zqf.core.entity.BuyerCart;
import cn.zqf.core.entity.Result;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class BuyerCartController {
    //添加商品到购物车 @CrossOrigin注解相当于设置了响应头信息
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:8085",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId,Integer num){
        try {
            return new Result(true, "添加成功");
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
    //获取购物车所有的返回数据
    @RequestMapping("/findCartList")
    public List<BuyerCart> findCartList(){
        return null;
    }
}
