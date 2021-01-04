package com.dah.mq.transport.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.jms.Message;
import javax.jms.MessageListener;

public class CorpServerStatusListener implements MessageListener {

    @Autowired
    private JmsHandler jmsHandler;

    @Value("${corp.server.status.send.activemq.destinationName}")
    private String destinationName;

    @Value("${corp.server.status.send.activemq.pubSubDomain}")
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
