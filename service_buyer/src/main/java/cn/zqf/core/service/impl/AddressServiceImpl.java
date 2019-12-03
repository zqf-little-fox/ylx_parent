package cn.zqf.core.service.impl;

import cn.zqf.core.dao.address.AddressDao;
import cn.zqf.core.pojo.address.Address;
import cn.zqf.core.pojo.address.AddressQuery;
import cn.zqf.core.service.AddressService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private AddressDao addressDao;

    @Override
    public List<Address> findListByLoginUser(String userName) {
        AddressQuery query = new AddressQuery();
        AddressQuery.Criteria criteria = query.createCriteria();
        criteria.andUserIdEqualTo(userName);
        List<Address> addressList = addressDao.selectByExample(query);
        return addressList;
    }
}
