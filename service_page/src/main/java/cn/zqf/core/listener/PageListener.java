package cn.zqf.core.listener;

import cn.zqf.core.service.CmsService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.util.Map;

public class PageListener implements MessageListener {
    @Autowired
    private CmsService cmsService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
        try{
            String goodsId = activeMQTextMessage.getText();
            Map<String, Object> goodsData = cmsService.findGoodsData(Long.valueOf(goodsId));
            cmsService.createStaticPage(Long.valueOf(goodsId),goodsData);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
