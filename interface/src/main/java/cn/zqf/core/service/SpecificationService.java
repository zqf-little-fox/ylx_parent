package cn.zqf.core.service;

import cn.zqf.core.entity.PageResult;
import cn.zqf.core.entity.SpecEntity;
import cn.zqf.core.pojo.specification.Specification;

import java.util.List;
import java.util.Map;

public interface SpecificationService {
    //查询所有 带分页 带条件
    PageResult search(Specification spec, Integer pageNum, Integer pageSize);

    void add(SpecEntity specEntity);

    SpecEntity findOne(Long id);

    void update(SpecEntity specEntity);

    void delete(Long[] ids);

    List<Map> selectOptionList();
}
