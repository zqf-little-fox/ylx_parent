package cn.zqf.core.service;

import cn.zqf.core.entity.PageResult;
import cn.zqf.core.pojo.ad.Content;

import java.util.List;

public interface ContentService {
    List<Content> findAll();

    PageResult findPage(Integer pageNum, Integer pageSize);

    void add(Content content);

    Content findOne(Long id);

    void update(Content content);

    PageResult search(Content content, Integer pageNum, Integer pageSize);

    void delete(Long[] ids);

    List<Content> findByCategoryId(Long categoryId);

    List<Content> findByCategoryIdFromRedis(Long categoryId);
}
