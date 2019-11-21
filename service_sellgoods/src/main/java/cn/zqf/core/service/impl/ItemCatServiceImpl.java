package cn.zqf.core.service.impl;

import cn.zqf.core.dao.item.ItemCatDao;
import cn.zqf.core.pojo.item.ItemCat;
import cn.zqf.core.pojo.item.ItemCatQuery;
import cn.zqf.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private ItemCatDao itemCatDao;

    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        ItemCatQuery query = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = query.createCriteria();
        criteria.andParentIdEqualTo(parentId);
        return itemCatDao.selectByExample(query);
    }

    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public void add(ItemCat itemCat) {
        itemCatDao.insertSelective(itemCat);
    }

    @Override
    public void update(ItemCat itemCat) {
        itemCatDao.updateByPrimaryKeySelective(itemCat);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            itemCatDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }
}
