package com.uazbot.service;

import com.uazbot.bot.Bot;
import com.uazbot.command.Command;
import com.uazbot.command.ParsedCommand;
import com.uazbot.command.Parser;
import com.uazbot.handler.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;

@Component
public class MessageReciever implements Runnable {
    private static final Logger log = Logger.getLogger(MessageReciever.class);
    private final int WAIT_FOR_NEW_MESSAGE_DELAY = 1000;

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

    @Override
    public void run() {
        log.info("[STARTED] MsgReciever.  Bot class: " + bot);
        while (true) {
            for (Object object = bot.receiveQueue.poll(); object != null; object = bot.receiveQueue.poll()) {
                log.debug("New object for analyze in queue " + object.toString());
                analyze(object);
            }
            try {
                Thread.sleep(WAIT_FOR_NEW_MESSAGE_DELAY);
            } catch (InterruptedException e) {
                log.error("Catch interrupt. Exit", e);
                return;
            }
        }
    }

    private void analyze(Object object) {
        if (object instanceof Update) {
            Update update = (Update) object;
            log.debug("Update recieved: " + update.toString());
            analyzeForUpdateType(update);
        } else log.warn("Cant operate type of object: " + object.toString());
    }

    private void analyzeForUpdateType(Update update) {
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
            bot.sendQueue.add(messageOut);
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
            case REG:
            case LIST:
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
