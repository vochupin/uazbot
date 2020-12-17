package com.uazbot.restservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:credentials.properties")
public class AppConfig {

    @Value( "${jdbc.url}" )
    private String jdbcUrl;

    @Value( "${bot.admin.chat.id}" )
    private String botAdminChatId;

    @Value( "${bot.token}" )
    private String botToken;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getBotAdminChatId() {
        return botAdminChatId;
    }

    public void setBotAdminChatId(String botAdminChatId) {
        this.botAdminChatId = botAdminChatId;
    }

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
}
