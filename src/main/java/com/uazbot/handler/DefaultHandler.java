package com.uazbot.handler;

import com.uazbot.command.ParsedCommand;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DefaultHandler implements UpdateHandler {
    private static final Logger log = Logger.getLogger(DefaultHandler.class);

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        return "";
    }
}
