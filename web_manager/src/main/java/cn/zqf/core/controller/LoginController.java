package cn.zqf.core.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    //显示用户名
    @RequestMapping("/showName")
    public Map showName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String,Object> map = new HashMap<>();
        map.put("username",name);
        return map;
    }
}
