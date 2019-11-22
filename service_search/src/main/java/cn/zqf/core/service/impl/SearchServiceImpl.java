package cn.zqf.core.service.impl;

import cn.zqf.core.pojo.item.Item;
import cn.zqf.core.service.SearchService;
import cn.zqf.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    //solr 高亮显示 查询
    @Override
    public Map<String, Object> search(Map paramMap) {
        //根据参数关键字 到solr中查询 分页 总条数 总页数
        Map<String, Object> resultMap = highLightSearch(paramMap);
        //根据查询的参数到solr中获取对应的分类结果 因为分类有重复 按分组的方式去重复
        List<String> categoryList = findGroupCategoryList(paramMap);
        resultMap.put("categoryList",categoryList);
        //判断paramMap传入的参数中是否有分类的名称
        String category = String.valueOf(paramMap.get("category"));
        if (category != null && !"".equals(category)){
            //如果有分类参数 根据分类查询对应的品牌集合和规格集合
            Map specListAndBrandList = findSpecListAndBrandList(category);
            resultMap.putAll(specListAndBrandList);
        }else {
            //如果没有 根据第一个分类查询对应的商品集合
            Map specListAndBrandList = findSpecListAndBrandList(categoryList.get(0));
            resultMap.putAll(specListAndBrandList);
        }
        return resultMap;
    }

    //根据参数关键字 到solr中查询 分页 总条数 总页数 高亮显示
    private Map<String, Object> highLightSearch(Map paramMap){
        //获取关键字
        String keywords = String.valueOf(paramMap.get("keywords"));
        //当前页
        Integer pageNo = (Integer) paramMap.get("pageNo");
        //每页查询多少条
        Integer pageSize = (Integer) paramMap.get("pageSize");
        //封装查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //查询的条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询条件放入查询对象中
        query.addCriteria(criteria);
        //计算从第几条开始查询
        if (pageNo == null || pageNo <= 0){
            pageNo = 1;
        }
        //起始页
        Integer start = (pageNo-1)*pageSize;
        //设置从第几条开始
        query.setOffset(start);
        //每页查询多少条数据
        query.setRows(pageSize);

        //创建高亮显示对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //设置哪个域需要高亮显示
        highlightOptions.addField("item_title");
        //设置高亮前缀
        highlightOptions.setSimplePrefix("<em style=\"color:red;\">");
        //设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //将高亮加入到查询对象中
        query.setHighlightOptions(highlightOptions);

        //去solr查询并返回结果
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query, Item.class);
        //获取带高亮的集合
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();
        //封装查询的结果集
        List<Item> itemList = new ArrayList<>();
        //遍历高亮集合
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            Item item = itemHighlightEntry.getEntity();
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
            if (highlights != null && highlights.size() > 0){
                //获取高亮的标题集合
                List<String> highLightTitle = highlights.get(0).getSnipplets();
                if (highLightTitle != null && highLightTitle.size() > 0){
                    //获取高亮的标题
                    String title = highLightTitle.get(0);
                    item.setTitle(title);
                }
            }
            itemList.add(item);
        }
        Map<String, Object> resultMap = new HashMap<>();
        //查询到的结果集
        resultMap.put("rows",itemList);
        //总页数
        resultMap.put("totalPages",items.getTotalPages());
        //总条数
        resultMap.put("total",items.getTotalElements());
        return resultMap;
    }

    //根据查询的参数到solr中获取对应的分类结果 因为分类有重复 按分组的方式去重复
    private List<String> findGroupCategoryList(Map paramMap){
        //获取关键字
        String keywords = String.valueOf(paramMap.get("keywords"));
        //创建查询对象
        SimpleQuery query = new SimpleQuery();
        //创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        //将查询条件放入查询对象
        query.addCriteria(criteria);

        //封装分类的结果集
        List<String> resultList = new ArrayList<>();

        //创建分组对象
        GroupOptions options = new GroupOptions();
        //设置根据分类域进行分组
        options.addGroupByField("item_category");
        //将分组对象放入到查询对象中
        query.setGroupOptions(options);
        //使用solr 分组查询 分类的集合
        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        //获得结果集合中的分类域集合
        GroupResult<Item> item_category = items.getGroupResult("item_category");
        //获取分类域中的实体集合
        Page<GroupEntry<Item>> groupEntries = item_category.getGroupEntries();
        //遍历实体集合 得到实体对象
        for (GroupEntry<Item> groupEntry : groupEntries) {
            String groupCategory = groupEntry.getGroupValue();
            //实体对象 组装到 集合中
            resultList.add(groupCategory);
        }
        return resultList;
    }

    //根据分类名称查询对应的品牌集合和规格集合
    private Map findSpecListAndBrandList(String categoryName){
        //根据分类名称到redis中查询对应的模板id
        Long templateId = (Long) redisTemplate.boundHashOps(Constants.CATEGORY_LIST_REDIS).get(categoryName);
        //根据模板id到redis中查询对应的品牌集合
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps(Constants.BRAND_LIST_REDIS).get(templateId);
        //根据模板id到redis中查询对应的规格集合
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps(Constants.SPEC_LIST_REDIS).get(templateId);
        //将品牌集合和规格集合封装到Map中 返回
        Map resultMap = new HashMap();
        resultMap.put("brandList",brandList);
        resultMap.put("specList",specList);
        return resultMap;
    }

    /*solr查询
    @Override
    public Map<String, Object> search(Map paramMap) {
        //获取查询条件
        String keywords = (String) paramMap.get("keywords");
        //当前页
        Integer pageNo = (Integer) paramMap.get("pageNo");
        //每页查询多少条
        Integer pageSize = (Integer) paramMap.get("pageSize");
        //封装查询对象 将查询条件放入查询对象
        SimpleQuery query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(keywords);
        query.addCriteria(criteria);
        //计算从第几条开始查询
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
    }*/

}
