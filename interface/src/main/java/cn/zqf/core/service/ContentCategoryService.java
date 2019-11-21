package cn.zqf.core.service;

import cn.zqf.core.entity.PageResult;
import cn.zqf.core.pojo.ad.ContentCategory;

import java.util.List;

public interface ContentCategoryService {
    List<ContentCategory> findAll();

    PageResult findPage(Integer page, Integer rows);

    void add(ContentCategory contentCategory);

    ContentCategory findOne(Long id);

    void update(ContentCategory contentCategory);

    PageResult search(ContentCategory contentCategory, Integer pageNum, Integer pageSize);

    void delete(Long[] ids);
}
