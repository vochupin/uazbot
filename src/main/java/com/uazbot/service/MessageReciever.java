package com.uazbot.service;

import com.uazbot.bot.Bot;
import com.uazbot.command.Command;
import com.uazbot.command.ParsedCommand;
import com.uazbot.command.Parser;
import com.uazbot.handler.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

@Component
public class MessageReciever {
    private static final Logger log = Logger.getLogger(MessageReciever.class);

    @Autowired
    Bot bot;

    @Autowired
    Parser parser;

    @Autowired
    SystemHandler systemHandler;

    @Autowired
    DefaultHandler defaultHandler;

    @Autowired
    NotifyHandler notifyHandler;

    @Autowired
    EmojiHandler emojiHandler;

    @JmsListener(destination = "receiveHandler", containerFactory = "uazBotJmsListenerFactory")
    public void analyzeForUpdateType(Update update) {
        log.debug("Update recieved: " + update.toString());

        Message message = update.getMessage();
        Long chatId = message.getChatId();

        ParsedCommand parsedCommand = new ParsedCommand(Command.NONE, "");

        if (message.hasText()) {
            parsedCommand = parser.getParsedCommand(message.getText());
        } else {
            Sticker sticker = message.getSticker();
            if (sticker != null) {
                parsedCommand = new ParsedCommand(Command.STICKER, sticker.getFileId());
            }
        }

        UpdateHandler handlerForCommand = getHandlerForCommand(parsedCommand.getCommand());
        String operationResult = handlerForCommand.operate(chatId.toString(), parsedCommand, update);

        if (!"".equals(operationResult)) {
            SendMessage messageOut = new SendMessage();
            messageOut.setChatId(chatId.toString());
            messageOut.setText(operationResult);
            bot.sendMessage(messageOut);
        }
    }

    private UpdateHandler getHandlerForCommand(Command command) {
        if (command == null) {
            log.warn("Null command accepted. This is not good scenario.");
            return defaultHandler;
        }
        switch (command) {
            case START:
            case HELP:
            case ID:
            case FROM:
            case LIST:
            case BYPLACE:
            case BYNAME:
            case BYRANGE:
            case STICKER:
                log.info("Handler for command[" + command.toString() + "] is: " + systemHandler);
                return systemHandler;
            case NOTIFY:
                log.info("Handler for command[" + command.toString() + "] is: " + notifyHandler);
                return notifyHandler;
            case TEXT_CONTAIN_EMOJI:
                log.info("Handler for command[" + command.toString() + "] is: " + emojiHandler);
                return emojiHandler;
            default:
                log.info("Handler for command[" + command.toString() + "] not Set. Return DefaultHandler");
                return defaultHandler;
        }
    }
}
