package cn.zqf.core.controller;

import cn.zqf.core.pojo.ad.Content;
import cn.zqf.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;

    //广告查询
    @RequestMapping("/findByCategoryId")
    public List<Content> findByCategoryId(Long categoryId){
        //return contentService.findByCategoryId(categoryId);
        //从redis 广告查询
        List<Content> list = contentService.findByCategoryIdFromRedis(categoryId);
        return list;
    }
}
