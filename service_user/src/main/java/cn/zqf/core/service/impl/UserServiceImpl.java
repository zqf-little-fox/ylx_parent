package cn.zqf.core.service.impl;

import cn.zqf.core.dao.user.UserDao;
import cn.zqf.core.pojo.user.User;
import cn.zqf.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ActiveMQQueue ssDestination;
    @Value("${template_code}")
    private String template_code;
    @Value("${sign_name}")
    private String sign_name;
    @Autowired
    private UserDao userDao;
    
    @Override
    public void sendCode(String phone) {
        //1. 生成一个随机六位数
        StringBuffer sb = new StringBuffer();
        for (int i = 1; i < 7; i++){
            int num = new Random().nextInt(10);
            sb.append(num);
        }
        //2. 将手机号码为key 验证码为value 存到redis中 设置生存时间为10分钟
        redisTemplate.boundValueOps(phone).set(sb.toString(),60*10, TimeUnit.SECONDS);
        final String smsCode = sb.toString();
        //3. 将手机号码 短信内容 模板编号 签名 封装到map消息 发送给消息服务器
        jmsTemplate.send(ssDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage message = session.createMapMessage();
                message.setString("mobile",phone);
                message.setString("template_code",template_code);
                message.setString("sign_name",sign_name);
                Map map = new HashMap();
                map.put("code",smsCode);
                message.setString("param", JSON.toJSONString(map));
                return (Message) message;
            }
        });
    }

    @Override
    public boolean checkCode(String phone, String smsCode) {
        if (phone == null || smsCode == null || "".equals(phone) || "".equals(smsCode)) {
            return false;
        }
        //1. 到redis中获取刚刚存取的验证码
        String redisSmsCode = (String) redisTemplate.boundValueOps(phone).get();
        //2. 判断页面传入的数据和从redis中取出的验证码是否相等
        if (smsCode.equals(redisSmsCode)){
            return true;
        }
        return false;
    }

    @Override
    public void add(User user) {
        userDao.insertSelective(user);
    }
}
