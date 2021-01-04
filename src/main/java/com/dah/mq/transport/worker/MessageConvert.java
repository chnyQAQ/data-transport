package com.dah.mq.transport.worker;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.jms.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class MessageConvert {

    private static final Logger logger = LoggerFactory.getLogger(MessageConvert.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String convertMessage(Message message) {
        String msg = "";
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                msg = textMessage.getText();
            } else if (message instanceof MapMessage) {
                MapMessage mapMessage = (MapMessage) message;
                String name;
                Map<Object, Object> map = new HashMap<>();
                for (Enumeration iter = mapMessage.getMapNames(); iter.hasMoreElements(); map.put(name, mapMessage.getObject(name))) {
                    name = iter.nextElement().toString();
                }
                msg = JSON.toJSONString(map);
            } else if (message instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                msg = JSON.toJSONString(objectMessage.getObject());
            } else if (message instanceof BytesMessage) {
                BytesMessage bytesMessage = (BytesMessage) message;
                byte[] bs = new byte[1024];
                while (bytesMessage.readBytes(bs) != -1) {
                    msg = new String(bs);
                }
            } else if (message instanceof StreamMessage) {
                StreamMessage streamMessage = (StreamMessage) message;
                StringBuilder stringBuilder = new StringBuilder();
                while (StringUtils.isEmpty(msg = streamMessage.readString())) {
                    stringBuilder.append(msg);
                }
                msg = stringBuilder.toString();
            } else {
                logger.error("消息数据类型无法识别 - " + message);
            }
        } catch (Exception e) {
            logger.error("消息数据类型转换失败 - ", message, e.getMessage());
        }
        return msg;
    }
}
