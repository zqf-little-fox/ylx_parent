package cn.zqf.core.service.impl;

import cn.zqf.core.dao.ad.ContentDao;
import cn.zqf.core.entity.PageResult;
import cn.zqf.core.pojo.ad.Content;
import cn.zqf.core.pojo.ad.ContentQuery;
import cn.zqf.core.service.ContentService;
import cn.zqf.core.util.Constants;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private ContentDao contentDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Content> findAll() {
        return contentDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        ContentQuery query = new ContentQuery();
        Page<Content> page = (Page<Content>) contentDao.selectByExample(query);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(Content content) {
        //添加广告 到数据库
        contentDao.insertSelective(content);
        //根据分类id到redis中删除对应的分类广告集合
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
    }

    @Override
    public Content findOne(Long id) {
        return contentDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Content content) {
        //根据广告id到数据库中查询原来的广告对象
        Content oldContent = contentDao.selectByPrimaryKey(content.getId());
        //根据原来广告对象中的分类id 到redis 删除对应广告集合数据
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(oldContent.getCategoryId());
        //根据传入的最新广告分类对中的id删除redis中对应的广告集合数据
        redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
        //将新的广告对象更新到数据库中
        contentDao.updateByPrimaryKeySelective(content);
    }

    @Override
    public PageResult search(Content content, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        if (content != null){
            if (content.getTitle() != null && !"".equals(content.getTitle())){
                criteria.andTitleLike("%" + content.getTitle() + "%");
            }
        }
        Page<Content> page = (Page<Content>) contentDao.selectByExample(query);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void delete(Long[] ids) {
        if (ids != null){
            for (Long id : ids) {
                Content content = contentDao.selectByPrimaryKey(id);
                //根据广告对象中的分类id 删除redis中对应的广告集合数据
                redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).delete(content.getCategoryId());
                //删除广告
                contentDao.deleteByPrimaryKey(id);
            }
        }
    }

    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        criteria.andCategoryIdEqualTo(categoryId);
        return contentDao.selectByExample(query);
    }

    @Override
    public List<Content> findByCategoryIdFromRedis(Long categoryId) {
        //根据分类的id到redis中取数据
        List<Content> contentList = (List<Content>) redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).get(categoryId);
        //如果redis中没有数据 到数据库中取数据
        if (contentList == null){
            //如果数据库中获取到数据 存入redis中
            contentList = findByCategoryId(categoryId);
            redisTemplate.boundHashOps(Constants.CONTENT_LIST_REDIS).put(categoryId,contentList);
        }
        return contentList;
    }
}