package cn.zqf.core.controller;

import cn.zqf.core.pojo.template.TypeTemplate;
import cn.zqf.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
    @Reference
    private TypeTemplateService typeTemplateService;

    //根据主键查询
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }

    @RequestMapping("/findBySpecList")
    public List<Map> findBySpecList(Long id){
        return typeTemplateService.findBySpecList(id);
    }
}
