package com.uazbot.handler;

import com.uazbot.bot.Bot;
import com.uazbot.command.Command;
import com.uazbot.command.ParsedCommand;
import com.uazbot.entity.Person;
import com.uazbot.restservice.AppConfig;
import com.uazbot.service.PersonService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import java.util.List;
import java.util.Optional;

@Component
public class SystemHandler implements UpdateHandler {
    private static final Logger log = Logger.getLogger(SystemHandler.class);
    private final String END_LINE = "\n";

    @Autowired
    AppConfig appConfig;

    @Autowired
    Bot bot;

    @Autowired
    PersonService personService;

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Command command = parsedCommand.getCommand();

        switch (command) {
            case START:
                bot.sendQueue.add(getMessageStart(chatId));
                break;
            case HELP:
                bot.sendQueue.add(getMessageHelp(chatId));
                break;
            case REG:
                User user = update.getMessage().getFrom();

                Optional<Person> optionalPerson = personService.getPersonById(user.getId().longValue());
                Person person;
                if (optionalPerson.isPresent()) {
                    person = optionalPerson.get();
                } else {
                    person = new Person();
                    person.setPid(user.getId().longValue());
                }

                person.setFirstName(user.getFirstName());
                person.setLastName(user.getLastName());
                person.setUserName(user.getUserName());
                person.setFromWhere(parsedCommand.getText());

                personService.createPerson(person);

                return "Запись по персоне создана: " + person.toString();
            case LIST:
                StringBuilder sb = new StringBuilder();
                List<Person> personList = personService.list();
                for (Person p : personList) {
                    sb.append(p + "\n");
                }

                return "Зареганы: " + sb.toString();
            case ID:
                return "Ваш telegramID: " + update.getMessage().getFrom().getId();
            case STICKER:
                return "StickerID: " + parsedCommand.getText();
        }
        return "";
    }

    private SendMessage getMessageHelp(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);

        StringBuilder text = new StringBuilder();
        text.append("*This is help message*").append(END_LINE).append(END_LINE);
        text.append("[/start](/start) - show start message").append(END_LINE);
        text.append("[/help](/help) - show help message").append(END_LINE);
        text.append("[/id](/id) - know your ID in telegram ").append(END_LINE);
        text.append("/*notify* _time-in-sec_  - receive notification from me after the specified time").append(END_LINE);

        sendMessage.setText(text.toString());
        return sendMessage;
    }

    private SendMessage getMessageStart(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);
        StringBuilder text = new StringBuilder();
        text.append("Привет! Я  *").append(appConfig.getBotName()).append("*").append(END_LINE);
        text.append("Я создан специально для чата патроводов.").append(END_LINE);
        text.append("Все что я могу, можно узнать по команде [/help](/help)");
        sendMessage.setText(text.toString());
        return sendMessage;
    }
}
