package cn.zqf.core.controller;

import cn.zqf.core.entity.PageResult;
import cn.zqf.core.entity.Result;
import cn.zqf.core.pojo.seller.Seller;
import cn.zqf.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference
    private SellerService sellerService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody Seller seller, Integer page, Integer rows){
        return sellerService.search(seller,page,rows);
    }

    //根据id查询
    @RequestMapping("/findOne")
    public Seller findOne(String sellerId){
        return sellerService.findOne(sellerId);
    }

    //商家审核
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId, String status){
        try{
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
}
