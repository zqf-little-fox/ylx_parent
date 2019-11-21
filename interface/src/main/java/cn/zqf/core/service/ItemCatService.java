package cn.zqf.core.service;

import cn.zqf.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {
    List<ItemCat> findByParentId(Long parentId);

    ItemCat findOne(Long id);

    void add(ItemCat itemCat);

    void update(ItemCat itemCat);

    void delete(Long[] ids);

    List<ItemCat> findAll();
}
