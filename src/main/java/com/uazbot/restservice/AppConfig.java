package com.uazbot.restservice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@PropertySource("classpath:credentials.properties")
public class AppConfig {

    @Value( "${bot.admin.chat.id}" )
    private String botAdminChatId;

    @Value( "${bot.token}" )
    private String botToken;

    @Value("${bot.name}" )
    private String botName;

    @Value("${nominatim.server.url}")
    private String nominatimServerUrl;

    @Value("${nominatim.header.email}")
    private String nominatimHeaderEmail;

}
