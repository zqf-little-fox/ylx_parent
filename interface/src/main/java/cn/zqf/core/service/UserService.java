package cn.zqf.core.service;

import cn.zqf.core.pojo.user.User;

public interface UserService {
    void sendCode(String phone);
    boolean checkCode(String phone, String smsCode);
    void add(User user);
}
