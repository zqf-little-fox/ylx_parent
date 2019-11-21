package cn.zqf.core.service.impl;

import cn.zqf.core.pojo.item.Item;
import cn.zqf.core.service.SearchService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map<String, Object> search(Map paramMap) {
        //获取查询条件
        String keywords = (String) paramMap.get("keywords");
        //当前页
        Integer pageNo = (Integer) paramMap.get("pageNo");
        //每页查询多少条
        Integer pageSize = (Integer) paramMap.get("pageSize");
        //封装查询对象
        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询条件放入查询对象
        query.addCriteria(criteria);
        if (pageNo == null || pageNo <= 0){
            pageNo = 1;
        }
        //起始页
        Integer start = (pageNo-1)*pageSize;
        //设置从第几条开始
        query.setOffset(start);
        //每页查询多少条数据
        query.setRows(pageSize);
        //去solr查询并返回结果
        ScoredPage<Item> items = solrTemplate.queryForPage(query, Item.class);
        HashMap<String, Object> resultMap = new HashMap<>();
        //每页查询多少条数据
        resultMap.put("rows",items.getContent());
        //总页数
        resultMap.put("totalPages",items.getTotalPages());
        //总条数
        resultMap.put("total",items.getTotalElements());
        return resultMap;
    }
}
