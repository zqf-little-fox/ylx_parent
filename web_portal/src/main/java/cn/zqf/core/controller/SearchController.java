package cn.zqf.core.controller;

import cn.zqf.core.service.SearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class SearchController {
    @Reference
    private SearchService searchService;
    @RequestMapping("/search")
    public Map<String, Object> search(Map paramMap){
        return searchService.search(paramMap);
    }
}
