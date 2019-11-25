package cn.zqf.core.controller;

import cn.zqf.core.entity.GoodsEntity;
import cn.zqf.core.entity.PageResult;
import cn.zqf.core.entity.Result;

import cn.zqf.core.pojo.good.Goods;
import cn.zqf.core.service.GoodsService;
import cn.zqf.core.service.SolrManagerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private GoodsService goodsService;
    @Reference
    private SolrManagerService solrManagerService;

    @RequestMapping("/add")
    public Result add(@RequestBody GoodsEntity goodsEntity){
        try{
            //卖家名称初始化
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsEntity.getGoods().setSellerId(username);
            goodsService.add(goodsEntity);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    //查询商品列表
    @RequestMapping("/search")
    public PageResult search(@RequestBody Goods goods,Integer page, Integer rows){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(username);
        return goodsService.search(goods,page,rows);
    }

    //根据id查询
    @RequestMapping("/findOne")
    public GoodsEntity findOne(Long id){
        return goodsService.findOne(id);
    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody GoodsEntity goodsEntity){
        try{
            //获取当前登录名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //商品的所有者
            String sellerId = goodsEntity.getGoods().getSellerId();
            if (!username.equals(sellerId)){
                return new Result(false,"您没有权限修改");
            }
            goodsService.update(goodsEntity);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();;
            return new Result(false,"修改失败");
        }
    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            if (ids != null){
                for (Long id : ids) {
                    goodsService.delete(id);
                    //根据商品id 删除solr中的数据
                    solrManagerService.deleteItemFromSolr(id);
                }
            }
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //商品上下架
    @RequestMapping("/updateIsMarketable")
    public Result updateIsMarketable(Long[] ids,String isMarketable){
        try{
            if (ids != null) {
                for (Long id : ids) {
                    //根据商品id获取该商品数据
                    GoodsEntity goodsEntity = goodsService.findOne(id);
                    //商品审核通过才能进行上下架操作
                    if ("1".equals(goodsEntity.getGoods().getAuditStatus())) {
                        //商品上下架
                        goodsService.updateIsMarketable(id, isMarketable);
                        //商品上架 根据商品id将商品信息添加到solr中
                        if ("1".equals(isMarketable)){
                            solrManagerService.saveItemToSolr(id);
                        //商品未上架或者已下架 根据商品id修改solr中对应的数据
                        } else{
                            solrManagerService.deleteItemFromSolr(id);
                        }
                    }else {
                        return new Result(false,"该商品未审核通过");
                    }
                }
            }
            return new Result(true,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
}
