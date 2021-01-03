package com.uazbot.bot;

import com.uazbot.AppConfig;
import com.uazbot.service.PersonService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class Bot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(Bot.class);
    private final int RECONNECT_PAUSE = 10000;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    AppConfig appConfig;

    @Autowired
    PersonService personService;

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Receive new Update. updateID: " + update.getUpdateId());
        jmsTemplate.convertAndSend("receiveHandler", update);
    }

    @Override
    public String getBotUsername() {
        log.debug("Bot name: " + appConfig.getBotName());
        return appConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        log.debug("Bot token: " + appConfig.getBotToken());
        return appConfig.getBotToken();
    }

    public void sendMessage(PartialBotApiMethod<Message> message) {
        jmsTemplate.convertAndSend("sendHandler", message);
    }

    public void botConnect() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            telegramBotsApi.registerBot(this);
            log.info("[STARTED] TelegramAPI. Bot Connected. Bot class: " + this);
        } catch (TelegramApiException e) {
            log.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        }
    }
}
