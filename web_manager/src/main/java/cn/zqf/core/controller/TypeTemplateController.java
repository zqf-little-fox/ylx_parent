package cn.zqf.core.controller;

import cn.zqf.core.entity.PageResult;
import cn.zqf.core.entity.Result;
import cn.zqf.core.pojo.template.TypeTemplate;
import cn.zqf.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
    @Reference
    private TypeTemplateService typeTemplateService;

    //查询所有 带分页 带条件
    @RequestMapping("/search")
    public PageResult search(@RequestBody TypeTemplate typeTemplate, Integer page, Integer rows){
        return typeTemplateService.search(typeTemplate,page,rows);
    }

    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody TypeTemplate typeTemplate){
        try{
            typeTemplateService.add(typeTemplate);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    //根据主键查询
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }

    //修改
    @RequestMapping("/update")
    public Result update(@RequestBody TypeTemplate typeTemplate){
        try{
            typeTemplateService.update(typeTemplate);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            typeTemplateService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/selectOptionList")
    public List<Map> selectOptionList(){
        return typeTemplateService.selectOptionList();
    }
}
