package cn.zqf.core.service.impl;

import cn.zqf.core.dao.ad.ContentCategoryDao;
import cn.zqf.core.entity.PageResult;
import cn.zqf.core.pojo.ad.ContentCategory;
import cn.zqf.core.pojo.ad.ContentCategoryQuery;
import cn.zqf.core.service.ContentCategoryService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private ContentCategoryDao contentCategoryDao;

    @Override
    public List<ContentCategory> findAll() {
        return contentCategoryDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        ContentCategoryQuery query = new ContentCategoryQuery();
        Page<ContentCategory> page = (Page<ContentCategory>) contentCategoryDao.selectByExample(query);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(ContentCategory contentCategory) {
        contentCategoryDao.insertSelective(contentCategory);
    }

    @Override
    public ContentCategory findOne(Long id) {
        return contentCategoryDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(ContentCategory contentCategory) {
        contentCategoryDao.updateByPrimaryKeySelective(contentCategory);
    }

    @Override
    public PageResult search(ContentCategory contentCategory, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        ContentCategoryQuery query = new ContentCategoryQuery();
        ContentCategoryQuery.Criteria criteria = query.createCriteria();
        if (contentCategory != null){
            if (contentCategory.getName() != null && !"".equals(contentCategory.getName())){
                criteria.andNameLike("%" + contentCategory.getName() +"%");
            }
        }
        Page<ContentCategory> page = (Page<ContentCategory>) contentCategoryDao.selectByExample(query);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void delete(Long[] ids) {
        if (ids != null){
            for (Long id : ids) {
                contentCategoryDao.deleteByPrimaryKey(id);
            }
        }
    }
}
