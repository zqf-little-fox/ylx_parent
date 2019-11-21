package cn.zqf.core.controller;

import cn.zqf.core.entity.PageResult;
import cn.zqf.core.entity.Result;
import cn.zqf.core.pojo.good.Brand;
import cn.zqf.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference
    private BrandService brandService;

    //查询所有品牌列表
    @RequestMapping("/findAll")
    public List<Brand> findAll(){
        List<Brand> list = brandService.findAll();
        return list;
    }

    //分页
    @RequestMapping("/findByPage")
    public PageResult findPage(Integer page,Integer rows){
        return brandService.findPage(page,rows);
    }

    //添加品牌
    @RequestMapping("/save")
    public Result add(@RequestBody Brand brand){
        try{
            brandService.add(brand);
            return new Result(true,"添加成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    //查询对象
    @RequestMapping("/findById")
    public Brand findOne(Long id){
        Brand brand = brandService.findOne(id);
        return brand;
    }

    //更新品牌数据
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand){
        try{
            brandService.update(brand);
            return new Result(true,"修改成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //条件查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody Brand brand,Integer page, Integer rows){
        return brandService.findPage(brand,page,rows);
    }

    //批量删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            brandService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        List<Map> brandList = brandService.selectOptionList();
        return brandList;
    }
}
