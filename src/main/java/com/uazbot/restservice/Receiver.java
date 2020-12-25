package com.uazbot.restservice;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class Receiver {

    @JmsListener(destination = "updateHandler", containerFactory = "uazBotJmsListenerFactory")
    public void receiveMessage(Update update) {
        System.out.println("Received <" + update + ">");
    }

}