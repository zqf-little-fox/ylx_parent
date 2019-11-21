package cn.zqf.core.service.impl;

import cn.zqf.core.dao.specification.SpecificationDao;
import cn.zqf.core.dao.specification.SpecificationOptionDao;
import cn.zqf.core.entity.PageResult;
import cn.zqf.core.entity.SpecEntity;
import cn.zqf.core.pojo.specification.Specification;
import cn.zqf.core.pojo.specification.SpecificationOption;
import cn.zqf.core.pojo.specification.SpecificationOptionQuery;
import cn.zqf.core.pojo.specification.SpecificationQuery;
import cn.zqf.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {
    @Autowired
    private SpecificationDao specificationDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public PageResult search(Specification spec, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        SpecificationQuery query = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = query.createCriteria();
        if (spec != null){
            if (spec.getSpecName() != null && !"".equals(spec.getSpecName())){
                criteria.andSpecNameLike("%" + spec.getSpecName() + "%");
            }
        }
        Page<Specification> page = (Page<Specification>) specificationDao.selectByExample(query);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(SpecEntity specEntity) {
        //添加规格对象
        specificationDao.insertSelective(specEntity.getSpecification());
        //添加规格选项对象
        if (specEntity.getSpecificationOptionList() != null) {
            for (SpecificationOption option : specEntity.getSpecificationOptionList()) {
                option.setSpecId(specEntity.getSpecification().getId());
                specificationOptionDao.insertSelective(option);
            }
        }
    }

    @Override
    public SpecEntity findOne(Long id) {
        //查询规格对象
        Specification specification = specificationDao.selectByPrimaryKey(id);
        //查询规格选项对象
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(query);
        return new SpecEntity(specification,specificationOptionList);
    }

    @Override
    public void update(SpecEntity specEntity) {
        specificationDao.updateByPrimaryKeySelective(specEntity.getSpecification());
        //根据规格id删除对应的规格选项集合数据
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(specEntity.getSpecification().getId());
        specificationOptionDao.deleteByExample(query);
        //将新的规格选项集合添加到规格选项表中
        if (specEntity.getSpecificationOptionList() != null){
            for (SpecificationOption option : specEntity.getSpecificationOptionList()) {
                //设置规格选项对象的外键
                option.setSpecId(specEntity.getSpecification().getId());
                specificationOptionDao.insertSelective(option);
            }
        }
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            specificationDao.deleteByPrimaryKey(id);
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            SpecificationOptionQuery.Criteria criteria = query.createCriteria();
            criteria.andSpecIdEqualTo(id);
            specificationOptionDao.deleteByExample(query);
        }
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }
}
