package cn.zqf.core.controller;

import cn.zqf.core.entity.PageResult;
import cn.zqf.core.entity.Result;
import cn.zqf.core.pojo.ad.ContentCategory;
import cn.zqf.core.service.ContentCategoryService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/contentCategory")
public class ContentCategoryController {
    @Reference
    private ContentCategoryService contentCategoryService;

    //查询所有列表
    @RequestMapping("/findAll")
    public List<ContentCategory> findAll(){
        List<ContentCategory> list = contentCategoryService.findAll();
        return list;
    }

    //分页
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer rows){
        return contentCategoryService.findPage(page,rows);
    }

    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody ContentCategory contentCategory){
        try{
            contentCategoryService.add(contentCategory);
            return new Result(true,"添加成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    //查询对象
    @RequestMapping("/findOne")
    public ContentCategory findOne(Long id){
        ContentCategory ContentCategory = contentCategoryService.findOne(id);
        return ContentCategory;
    }

    //更新数据
    @RequestMapping("/update")
    public Result update(@RequestBody ContentCategory contentCategory){
        try{
            contentCategoryService.update(contentCategory);
            return new Result(true,"修改成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //条件查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody ContentCategory contentCategory,Integer page, Integer rows){
        return contentCategoryService.search(contentCategory,page,rows);
    }

    //批量删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            contentCategoryService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
}
