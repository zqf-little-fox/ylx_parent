package cn.zqf.core.util;

import cn.zqf.core.dao.item.ItemDao;
import cn.zqf.core.pojo.item.Item;
import cn.zqf.core.pojo.item.ItemQuery;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataImportToSolr {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private ItemDao itemDao;

    //查询库存表中的所有数据
    private void importItemDataToSolr(){
        ItemQuery query = new ItemQuery();
        //mybatis 查询
        ItemQuery.Criteria criteria = query.createCriteria();
        criteria.andStatusEqualTo("1");
        List<Item> itemList = itemDao.selectByExample(query);

        if (itemList != null){
            for (Item item : itemList) {
                //获取规格 json 字符串
                String specJsonStr = item.getSpec();
                Map map = JSON.parseObject(specJsonStr, Map.class);
                item.setSpecMap(map);
            }
        }
        //放入solr
        solrTemplate.saveBeans(itemList);
        //提交
        solrTemplate.commit();
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        DataImportToSolr bean = (DataImportToSolr) context.getBean("dataImportToSolr");
        bean.importItemDataToSolr();
    }
}

