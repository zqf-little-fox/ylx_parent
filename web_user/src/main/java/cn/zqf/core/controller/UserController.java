package cn.zqf.core.controller;

import cn.zqf.core.entity.Result;
import cn.zqf.core.pojo.user.User;
import cn.zqf.core.service.UserService;
import cn.zqf.core.util.PhoneFormatCheckUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserService userService;

    //发送短信验证码 手机号
    @RequestMapping("/sendCode")
    public Result sendCode(String phone){
        try{
            //手机号码验证
            if (phone == null || "".equals(phone)) {
                return new Result(false,"手机号码不能为空");
            }
            if (!PhoneFormatCheckUtils.isPhoneLegal(phone)){
               return new Result(false,"手机格式不正确");
            }
            userService.sendCode(phone);
            return new Result(true,"发送成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"发送失败");
        }
    }

    @RequestMapping("/add")
    public Result add(@RequestBody User user, String smscode){
        try{
            boolean isCheck = userService.checkCode(user.getPhone(), smscode);
            if (!isCheck){
                return new Result(false,"手机或者验证码不正确");
            }
            user.setSourceType("1");
            user.setStatus("Y");
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userService.add(user);
            return new Result(true,"用户注册成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"用户注册失败");
        }
    }
}
