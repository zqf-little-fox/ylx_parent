package cn.zqf.core.controller;

import cn.zqf.core.entity.PageResult;
import cn.zqf.core.entity.Result;
import cn.zqf.core.entity.SpecEntity;
import cn.zqf.core.pojo.specification.Specification;
import cn.zqf.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecificationController {
    @Reference
    private SpecificationService specificationService;

    //查询所有  带分页 带条件
    @RequestMapping("/search")
    public PageResult search(@RequestBody Specification spec, Integer page, Integer rows){
        return specificationService.search(spec,page,rows);
    }

    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody SpecEntity specEntity){
        try{
            specificationService.add(specEntity);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    //根据主键查询
    @RequestMapping("/findOne")
    public SpecEntity findOne(Long id){
        return specificationService.findOne(id);
    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody SpecEntity specEntity){
        try{
            specificationService.update(specEntity);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //批量删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            specificationService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    //查询所有列表
    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        List<Map> specList = specificationService.selectOptionList();
        return specList;
    }
}
