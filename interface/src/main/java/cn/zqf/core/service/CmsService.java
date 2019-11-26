package cn.zqf.core.service;

import java.util.Map;

public interface CmsService {
    //取数据
    Map<String, Object> findGoodsData(Long goodsId);
    //根据取到的数据生成页面
    void createStaticPage(Long goodsId,Map<String,Object> rootMap) throws Exception;
}
