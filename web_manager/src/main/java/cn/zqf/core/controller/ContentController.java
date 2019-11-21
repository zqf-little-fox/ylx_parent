package cn.zqf.core.controller;

import cn.zqf.core.entity.PageResult;
import cn.zqf.core.entity.Result;
import cn.zqf.core.pojo.ad.Content;
import cn.zqf.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;

    //查询所有列表
    @RequestMapping("/findAll")
    public List<Content> findAll(){
        List<Content> list = contentService.findAll();
        return list;
    }

    //分页
    @RequestMapping("/findPage")
    public PageResult findPage(Integer page, Integer rows){
        return contentService.findPage(page,rows);
    }

    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody Content content){
        try{
            contentService.add(content);
            return new Result(true,"添加成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    //查询对象
    @RequestMapping("/findOne")
    public Content findOne(Long id){
        Content Content = contentService.findOne(id);
        return Content;
    }

    //更新数据
    @RequestMapping("/update")
    public Result update(@RequestBody Content content){
        try{
            contentService.update(content);
            return new Result(true,"修改成功");
        }catch(Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    //条件查询
    @RequestMapping("/search")
    public PageResult search(@RequestBody Content content,Integer page, Integer rows){
        return contentService.search(content,page,rows);
    }

    //批量删除
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            contentService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
}
