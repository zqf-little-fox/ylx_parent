package cn.zqf.core.service;

import cn.zqf.core.pojo.address.Address;

import java.util.List;

public interface AddressService {
    List<Address> findListByLoginUser(String userName);
}
