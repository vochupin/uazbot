package com.uazbot.restservice;

import com.uazbot.bot.Bot;
import com.uazbot.service.MessageReciever;
import com.uazbot.service.MessageSender;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.api.methods.send.SendMessage;

@SpringBootApplication
@ComponentScan(basePackages = {"com.uazbot"})
@EntityScan(basePackages = {"com.uazbot.entity"})
@EnableJpaRepositories("com.uazbot.repository")
public class RestServiceApplication {
    private static final Logger log = Logger.getLogger(RestServiceApplication.class);
    private static final int PRIORITY_FOR_SENDER = 1;
    private static final int PRIORITY_FOR_RECEIVER = 3;

    @Autowired
    AppConfig appConfig;

    public static void main(String[] args) {
        SpringApplication.run(RestServiceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        ApiContextInitializer.init();
        Bot test_habr_bot = new Bot("uazchatbot", appConfig.getBotToken());

        MessageReciever messageReciever = new MessageReciever(test_habr_bot);
        MessageSender messageSender = new MessageSender(test_habr_bot);

        test_habr_bot.botConnect();

        Thread receiver = new Thread(messageReciever);
        receiver.setDaemon(true);
        receiver.setName("MsgReciever");
        receiver.setPriority(PRIORITY_FOR_RECEIVER);
        receiver.start();

        Thread sender = new Thread(messageSender);
        sender.setDaemon(true);
        sender.setName("MsgSender");
        sender.setPriority(PRIORITY_FOR_SENDER);
        sender.start();

        sendStartReport(test_habr_bot);
    }

    private void sendStartReport(Bot bot) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(appConfig.getBotAdminChatId());
        sendMessage.setText("Запустился");
        bot.sendQueue.add(sendMessage);
    }
}
