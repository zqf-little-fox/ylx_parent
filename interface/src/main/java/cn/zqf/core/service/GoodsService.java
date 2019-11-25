package cn.zqf.core.service;

import cn.zqf.core.entity.GoodsEntity;
import cn.zqf.core.entity.PageResult;
import cn.zqf.core.pojo.good.Goods;

public interface GoodsService {
    GoodsEntity findOne(Long id);

    void add(GoodsEntity goodsEntity);

    PageResult search(Goods goods, Integer pageNum, Integer pageSize);

    void delete(Long id);

    void updateStatus(Long id, String status);

    void update(GoodsEntity goodsEntity);

    void updateIsMarketable(Long id, String isMarketable);
}
