package com.uazbot.service;

import com.uazbot.bot.Bot;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MessageSender {
    private static final Logger log = Logger.getLogger(MessageSender.class);

    @Autowired
    private Bot bot;

    @JmsListener(destination = "sendHandler", containerFactory = "uazBotJmsListenerFactory")
    private void send(PartialBotApiMethod<Message> object) {
        try {
            MessageType messageType = messageType(object);
            switch (messageType) {
                case EXECUTE:
                    BotApiMethod<Message> message = (BotApiMethod<Message>) object;
                    log.debug("Use Execute for " + object);
                    bot.execute(message);
                    break;
                case STICKER:
                    SendSticker sendSticker = (SendSticker) object;
                    log.debug("Use SendSticker for " + object);
                    bot.execute(sendSticker);
                    break;
                default:
                    log.warn("Cant detect type of object. " + object);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private MessageType messageType(PartialBotApiMethod<Message> object) {
        if (object instanceof SendSticker) return MessageType.STICKER;
        if (object instanceof BotApiMethod) return MessageType.EXECUTE;
        return MessageType.NOT_DETECTED;
    }

    enum MessageType {
        EXECUTE, STICKER, NOT_DETECTED,
    }
}
