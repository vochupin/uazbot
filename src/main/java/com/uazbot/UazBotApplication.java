package com.uazbot;

import com.uazbot.bot.Bot;
import com.uazbot.restservice.AppConfig;
import com.uazbot.service.PersonService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.jms.ConnectionFactory;

@SpringBootApplication
@EntityScan(basePackages = {"com.uazbot.entity"})
@EnableJpaRepositories("com.uazbot.repository")
@EnableJms
public class UazBotApplication {
    private static final Logger log = Logger.getLogger(UazBotApplication.class);

    @Autowired
    AppConfig appConfig;

    @Autowired
    PersonService personService;

    @Autowired
    Bot uazBot;

    @Bean
    public JmsListenerContainerFactory<?> uazBotJmsListenerFactory(@Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory,
                                                    DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        // This provides all boot's default to this factory, including the message converter
        configurer.configure(factory, connectionFactory);
        // You could still override some of Boot's default if necessary.
        return factory;
    }

    @Bean // Serialize message content to json using TextMessage
    public MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        return converter;
    }

    public static void main(String[] args) {
        SpringApplication.run(UazBotApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {

        uazBot.botConnect();

        sendStartReport(uazBot);
    }

    private void sendStartReport(Bot bot) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(appConfig.getBotAdminChatId());
        sendMessage.setText("Запустился");

        bot.sendMessage(sendMessage);
    }
}
