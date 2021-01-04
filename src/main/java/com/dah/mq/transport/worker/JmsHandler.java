package com.dah.mq.transport.worker;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Component
public class JmsHandler {

    @Autowired
    private JmsTemplate jmsTemplate;

    public void sendTopic(String destinationName, final String message) {
        jmsTemplate.send(new ActiveMQTopic(destinationName), new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(message);
            }
        });
    }

    public void sendQueue(String destinationName, final String message) {
        jmsTemplate.send(new ActiveMQQueue(destinationName), new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(message);
            }
        });
    }

}
