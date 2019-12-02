package cn.zqf.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

/*
    自定义认证类
    在此之前 负责用户密码和校验工作
    现在cas和SpringSecurity集成 集成之后 用户密码交给cas管理
 */
public class UserDetailServiceImpl implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ArrayList<GrantedAuthority> authorityList = new ArrayList<>();
        //向权限集合中加入访问权限
        authorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new User(username,"",authorityList);
    }
}
