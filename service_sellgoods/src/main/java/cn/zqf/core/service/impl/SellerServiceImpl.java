package cn.zqf.core.service.impl;

import cn.zqf.core.dao.seller.SellerDao;
import cn.zqf.core.entity.PageResult;
import cn.zqf.core.pojo.seller.Seller;
import cn.zqf.core.pojo.seller.SellerQuery;
import cn.zqf.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerDao sellerDao;

    @Override
    public void add(Seller seller) {
        //手动初始化时间
        seller.setCreateTime(new Date());
        //手动初始化状态 待审核
        seller.setStatus("0");
        sellerDao.insertSelective(seller);
    }

    @Override
    public PageResult search(Seller seller, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        SellerQuery query = new SellerQuery();
        SellerQuery.Criteria criteria = query.createCriteria();
        if (seller != null){
            if (seller.getStatus() != null && !"".equals(seller.getStatus())){
                criteria.andStatusEqualTo(seller.getStatus());
            }
            if (seller.getName() != null && !"".equals(seller.getName())){
                criteria.andNameLike("%" + seller.getName() + "%");
            }
            if (seller.getNickName() != null && !"".equals(seller.getNickName())){
                criteria.andNickNameLike("%" + seller.getNickName() + "%");
            }
        }
        Page<Seller> page = (Page<Seller>) sellerDao.selectByExample(query);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = sellerDao.selectByPrimaryKey(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }

    @Override
    public Seller findOne(String sellerId) {
        return sellerDao.selectByPrimaryKey(sellerId);
    }
}
