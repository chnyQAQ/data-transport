package com.dah.mq.transport.worker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.jms.Message;
import javax.jms.MessageListener;

public class GatewayHeartbeatListener implements MessageListener {

    @Autowired
    private JmsHandler jmsHandler;

    @Value("${gateway.heartbeat.send.activemq.destinationName}")
    private String destinationName;

    @Value("${gateway.heartbeat.send.activemq.pubSubDomain}")
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
