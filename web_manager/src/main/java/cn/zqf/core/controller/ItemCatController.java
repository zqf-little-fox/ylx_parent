package cn.zqf.core.controller;

import cn.zqf.core.entity.Result;
import cn.zqf.core.pojo.item.ItemCat;
import cn.zqf.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {
    @Reference
    private ItemCatService itemCatService;

    @RequestMapping("/findAll")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }

    @RequestMapping("/findByParentId")
    public List<ItemCat> findByParentId(Long parentId){
        return itemCatService.findByParentId(parentId);
    }

    @RequestMapping("/add")
    public Result add(@RequestBody ItemCat itemCat){
        try {
            itemCatService.add(itemCat);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    @RequestMapping("/findOne")
    public ItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody ItemCat itemCat){
        try{
            itemCatService.update(itemCat);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try{
            itemCatService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
}
