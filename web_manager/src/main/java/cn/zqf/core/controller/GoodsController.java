package cn.zqf.core.controller;

import cn.zqf.core.entity.GoodsEntity;
import cn.zqf.core.entity.PageResult;
import cn.zqf.core.entity.Result;
import cn.zqf.core.pojo.good.Goods;
import cn.zqf.core.service.CmsService;
import cn.zqf.core.service.GoodsService;
import cn.zqf.core.service.SolrManagerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private GoodsService goodsService;
    @Reference
    private SolrManagerService solrManagerService;
    @Reference
    private CmsService cmsService;

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
            if (ids != null){
                for (Long id : ids) {
                    //如果审核驳回，修改商品状态为未上架
                    if ("2".equals(status)){
                        goodsService.updateIsMarketable(id,null);
                    }
                    //根据商品的id改变商品的审核状态
                    goodsService.updateStatus(id,status);
                    if ("1".equals(status)){
                        //根据商品id获取库存数据 放入solr索引库中供搜索使用
                        solrManagerService.saveItemToSolr(id);
                        //根据商品的id获取商品的详情数据 并根据详情的数据和模板生成详情的页面
                        Map<String, Object> goodsData = cmsService.findGoodsData(id);
                        cmsService.createStaticPage(id,goodsData);
                    }
                }
            }
            return new Result(true,"成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }

    @RequestMapping("/testPage")
    public boolean testCreatePage(Long goodsId){
        try {
            Map<String, Object> goodsData = cmsService.findGoodsData(goodsId);
            cmsService.createStaticPage(goodsId, goodsData);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            if (ids != null){
                for (Long id : ids) {
                    goodsService.delete(id);
                    solrManagerService.deleteItemFromSolr(id);
                }
            }
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
}
