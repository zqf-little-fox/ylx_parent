package cn.zqf.core.service;

import cn.zqf.core.entity.PageResult;
import cn.zqf.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    public List<Brand> findAll();

    PageResult findPage(Integer pageNum, Integer pageSize);

    public void add(Brand brand);

    public Brand findOne(Long id);

    public void update(Brand brand);

    PageResult findPage(Brand brand, Integer pageNum, Integer pageSize);

    void delete(Long[] ids);

    List<Map> selectOptionList();
}
