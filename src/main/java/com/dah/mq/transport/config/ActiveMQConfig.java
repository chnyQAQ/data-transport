package com.dah.mq.transport.config;

import com.dah.mq.transport.worker.CorpServerStatusListener;
import com.dah.mq.transport.worker.GatewayHeartbeatListener;
import com.dah.mq.transport.worker.ProberStatusListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import javax.jms.ConnectionFactory;

@Configuration
public class ActiveMQConfig {

    @Value("${active.mq.ip:127.0.0.1}")
    private String ip;

    @Value("${active.mq.port:61616}")
    private String port;

    @Value("${active.mq.username:admin}")
    private String username;

    @Value("${active.mq.password:admin}")
    private String password;

    @Value("${listener.activemq.brokerURL}")
    private String activeMQBrokerURL;

    @Value("${prober.data.listener.activemq.destinationName}")
    private String proberStatusDestinationName;

    @Value("${prober.data.listener.activemq.pubSubDomain}")
    private Boolean proberStatusPubSubDomian;

    @Value("${corp.server.status.listener.activemq.destinationName}")
    private String corpServerStatusDestinationName;

    @Value("${corp.server.status.listener.activemq.pubSubDomain}")
    private Boolean corpServerStatusPubSubDomian;

    @Value("${gateway.heartbeat.listener.activemq.destinationName}")
    private String gatewayHeartbeatDestinationName;

    @Value("${gateway.heartbeat.listener.activemq.pubSubDomain}")
    private Boolean gatewayHeartbeatPubSubDomian;

    @Bean
    public ConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL("tcp://" + ip + ":" + port);
        connectionFactory.setUserName(username);
        connectionFactory.setPassword(password);
        PooledConnectionFactory poolFactory = new PooledConnectionFactory(connectionFactory);
        poolFactory.setMaxConnections(50);
        poolFactory.setMaximumActiveSessionPerConnection(50);
        poolFactory.setCreateConnectionOnStartup(true);
        //poolFactory.setTimeBetweenExpirationCheckMillis(3000);
        return poolFactory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setSessionTransacted(true);
        jmsTemplate.setReceiveTimeout(5000);
        return jmsTemplate;
    }

    @Bean
    public ConnectionFactory cachingConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(activeMQBrokerURL);
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(activeMQConnectionFactory);
        cachingConnectionFactory.setReconnectOnException(true);
        return cachingConnectionFactory;
    }

    @Bean
    public ProberStatusListener proberStatusListener() {
        return new ProberStatusListener();
    }

    @Bean
    public DefaultMessageListenerContainer proberStatusessageListenerContainer() {
        DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(cachingConnectionFactory());
        messageListenerContainer.setMessageListener(proberStatusListener());
        messageListenerContainer.setDestinationName(proberStatusDestinationName);
        messageListenerContainer.setPubSubDomain(proberStatusPubSubDomian);
        messageListenerContainer.setMaxConcurrentConsumers(10);
        return messageListenerContainer;
    }

    @Bean
    public CorpServerStatusListener corpServerStatusListener() {
        return new CorpServerStatusListener();
    }

    @Bean
    public DefaultMessageListenerContainer corpServerStatusMessageListenerContainer() {
        DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(cachingConnectionFactory());
        messageListenerContainer.setMessageListener(corpServerStatusListener());
        messageListenerContainer.setDestinationName(corpServerStatusDestinationName);
        messageListenerContainer.setPubSubDomain(corpServerStatusPubSubDomian);
        messageListenerContainer.setMaxConcurrentConsumers(10);
        return messageListenerContainer;
    }

    @Bean
    public GatewayHeartbeatListener gatewayHeartbeatListener() {
        return new GatewayHeartbeatListener();
    }

    @Bean
    public DefaultMessageListenerContainer gatewayHeartbeatMessageListenerContainer() {
        DefaultMessageListenerContainer messageListenerContainer = new DefaultMessageListenerContainer();
        messageListenerContainer.setConnectionFactory(cachingConnectionFactory());
        messageListenerContainer.setMessageListener(gatewayHeartbeatListener());
        messageListenerContainer.setDestinationName(gatewayHeartbeatDestinationName);
        messageListenerContainer.setPubSubDomain(gatewayHeartbeatPubSubDomian);
        messageListenerContainer.setMaxConcurrentConsumers(10);
        return messageListenerContainer;
    }

}
