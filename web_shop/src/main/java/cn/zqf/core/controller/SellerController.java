package cn.zqf.core.controller;

import cn.zqf.core.entity.Result;
import cn.zqf.core.pojo.seller.Seller;
import cn.zqf.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference
    private SellerService sellerService;

    //商家注册
    @RequestMapping("/add")
    public Result add(@RequestBody Seller seller){
        //密码加密
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String password = passwordEncoder.encode(seller.getPassword());
        seller.setPassword(password);
        try{
            sellerService.add(seller);
            return new Result(true,"添加成功");
        }catch (Exception e){
            return new Result(false,"添加失败");
        }
    }
}
