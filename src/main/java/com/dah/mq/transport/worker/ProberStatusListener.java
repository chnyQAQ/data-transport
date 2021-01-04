package com.dah.mq.transport.worker;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.jms.*;

public class ProberStatusListener implements MessageListener {

    @Autowired
    private JmsHandler jmsHandler;

    @Value("${prober.data.send.activemq.destinationName}")
    private String destinationName;

    @Value("${prober.data.send.activemq.pubSubDomain}")
    private Boolean pubSubDomain;

    @Override
    public void onMessage(Message message) {
        String msg = MessageConvert.convertMessage(message);
        if (!StringUtils.isEmpty(msg)) {
            if (pubSubDomain) {
                jmsHandler.sendTopic(destinationName, msg);
            } else {
                jmsHandler.sendQueue(destinationName, msg);
            }
        }
    }

}
