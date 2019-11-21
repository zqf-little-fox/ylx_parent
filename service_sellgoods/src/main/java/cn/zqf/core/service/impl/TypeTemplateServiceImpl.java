package cn.zqf.core.service.impl;

import cn.zqf.core.dao.specification.SpecificationOptionDao;
import cn.zqf.core.dao.template.TypeTemplateDao;
import cn.zqf.core.entity.PageResult;
import cn.zqf.core.pojo.specification.SpecificationOption;
import cn.zqf.core.pojo.specification.SpecificationOptionQuery;
import cn.zqf.core.pojo.template.TypeTemplate;
import cn.zqf.core.pojo.template.TypeTemplateQuery;
import cn.zqf.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {
    @Autowired
    private TypeTemplateDao typeTemplateDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public PageResult search(TypeTemplate typeTemplate, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        TypeTemplateQuery query = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = query.createCriteria();
        if (typeTemplate != null) {
            if (typeTemplate.getName() != null && typeTemplate.getName().length() > 0) {
                criteria.andNameLike("%" + typeTemplate.getName() + "%");
            }
        }
        Page<TypeTemplate> page = (Page<TypeTemplate>) typeTemplateDao.selectByExample(query);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(TypeTemplate typeTemplate) {

        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public List<Map> selectOptionList() {
        return typeTemplateDao.selectOptionList();
    }

    /**
     * 1.根据模板id 查询模板对象
     * 2.从模板对象中 获取规格数据 获取的是json数据
     * 3.将json专List集合
     * 4.遍历集合对象
     * 5.遍历 根据规格id查询对应规格选项数据
     * 6.将规格选项 在封装到规格选项中一起返回
     */
    @Override
    public List<Map> findBySpecList(Long id) {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        String specIds = typeTemplate.getSpecIds();
        List<Map> maps = JSON.parseArray(specIds, Map.class);
        if (maps != null){
            for (Map map : maps) {
                Long specId = Long.parseLong(String.valueOf(map.get("id")));
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                SpecificationOptionQuery.Criteria criteria = query.createCriteria();
                //根据规格id 获得规格选项数据
                criteria.andSpecIdEqualTo(specId);
                List<SpecificationOption> optionList = specificationOptionDao.selectByExample(query);
                //将规格封装到原来的map中
                map.put("options",optionList);
            }
        }
        return maps;
    }
}
