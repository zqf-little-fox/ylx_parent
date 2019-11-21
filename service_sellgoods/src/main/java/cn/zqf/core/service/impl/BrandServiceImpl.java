package cn.zqf.core.service.impl;

import cn.zqf.core.dao.good.BrandDao;
import cn.zqf.core.entity.PageResult;
import cn.zqf.core.pojo.good.Brand;
import cn.zqf.core.pojo.good.BrandQuery;
import cn.zqf.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandDao brandDao;

    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }


    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        Page<Brand> page =(Page<Brand>) brandDao.selectByExample(null);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(Brand brand) {
        brandDao.insert(brand);
    }

    @Override
    public Brand findOne(Long id) {
        Brand brand = brandDao.selectByPrimaryKey(id);
        return brand;
    }

    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    @Override
    public PageResult findPage(Brand brand, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        if (brand != null){
            if (brand.getName() != null && brand.getName().length() > 0){
                criteria.andNameLike("%" + brand.getName() + "%");
            }
            if(brand.getFirstChar() != null && brand.getFirstChar().length() > 0){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            brandDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public List<Map> selectOptionList() {
        return brandDao.selectOptionList();
    }
}
