package cn.zqf.core.listener;

import cn.zqf.core.service.SolrManagerService;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class ItemDeleteListener implements MessageListener {
    @Autowired
    private SolrManagerService solrManagerService;

    @Override
    public void onMessage(Message message) {
        ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
        try {
            String goodsId = activeMQTextMessage.getText();
            solrManagerService.deleteItemFromSolr(Long.valueOf(goodsId));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
