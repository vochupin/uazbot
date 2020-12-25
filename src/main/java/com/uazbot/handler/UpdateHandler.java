package com.uazbot.handler;

import com.uazbot.command.ParsedCommand;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {

    public abstract String operate(String chatId, ParsedCommand parsedCommand, Update update);
}
