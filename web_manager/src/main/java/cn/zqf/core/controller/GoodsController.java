package cn.zqf.core.controller;

import cn.zqf.core.entity.GoodsEntity;
import cn.zqf.core.entity.PageResult;
import cn.zqf.core.entity.Result;
import cn.zqf.core.pojo.good.Goods;
import cn.zqf.core.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private GoodsService goodsService;

    //查询商品列表
    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods,Integer page, Integer rows){
        return goodsService.search(goods,page,rows);
    }

    //根据id查询
    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id){
        return goodsService.findOne(id);
    }

    //审核
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status){
        try{
            goodsService.updateStatus(ids,status);
            return new Result(true,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            goodsService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
}
