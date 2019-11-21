package cn.zqf.core.service;

import cn.zqf.core.entity.PageResult;
import cn.zqf.core.pojo.seller.Seller;

public interface SellerService {
    //商品入驻
    void add(Seller seller);
    //查询所有列表 带分页 带条件
    PageResult search(Seller seller, Integer pageNum, Integer pageSize);
    //根据id查询
    Seller findOne(String sellerId);
    //审核
    void updateStatus(String sellerId, String status);
}
