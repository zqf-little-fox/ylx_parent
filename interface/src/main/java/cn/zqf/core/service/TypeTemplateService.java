package cn.zqf.core.service;

import cn.zqf.core.entity.PageResult;
import cn.zqf.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    PageResult search(TypeTemplate typeTemplate, Integer pageNum, Integer pageSize);

    void add(TypeTemplate typeTemplate);

    TypeTemplate findOne(Long id);

    void update(TypeTemplate typeTemplate);

    void delete(Long[] ids);

    List<Map> selectOptionList();

    List<Map> findBySpecList(Long id);
}
