package com.uazbot.handler;

import com.uazbot.bot.Bot;
import com.uazbot.command.Command;
import com.uazbot.command.ParsedCommand;
import com.uazbot.entity.Person;
import com.uazbot.AppConfig;
import com.uazbot.service.NominatimService;
import com.uazbot.service.PersonService;
import fr.dudie.nominatim.model.Address;
import org.apache.log4j.Logger;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    NominatimService nominatimService;

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Command command = parsedCommand.getCommand();

        Message message = update.getMessage();
        if (message == null) {
            message = update.getEditedMessage();
            if (message == null) {
                log.warn("Update without message: " + update.toString());
                return "";
            }
        }

        switch (command) {
            case START:
                bot.sendMessage(getMessageStart(chatId));
                break;

            case HELP:
                bot.sendMessage(getMessageHelp(chatId));
                break;

            case FROM:
                String text = parsedCommand.getText();
                if (StringUtils.isEmpty(text)) {
                    return "Команда */from* должна быть с местом откуда ты.";
                }

                Address address = nominatimService.getAddress(text);
                if (address == null) {
                    return "Регион не найден: " + text;
                }

                log.debug("Адрес получен: " + address.getDisplayName());

                User user = message.getFrom();

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
                person.setUserPlace(parsedCommand.getText());
                person.setOsmPlaceName(address.getDisplayName());

                person.setText(Stream.of(person.getFirstName(), person.getLastName(), person.getUserName(), person.getOsmPlaceName(), person.getUserPlace())
                .filter(Objects::nonNull)
                    .filter(Predicate.not(String::isBlank))
                    .collect(Collectors.joining(" ")));

                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                Point point = geometryFactory.createPoint(new Coordinate(address.getLongitude(), address.getLatitude()));
                person.setOsmMapPoint(point);

                personService.createOrUpdatePerson(person);

                return "Запись по персоне создана(изменена): \n" + person.getLongDescription();

            case LIST:
                return makeUserListMessage("Полный список участников:", personService.list());

            case INFO:
                if (parsedCommand.getText() == null || parsedCommand.getText().trim().isEmpty()) {
                    return "Должен быть параметр";
                }

                return makeUserListMessage("Найдено:", personService.findByName(parsedCommand.getText()));

            case BYRANGE:
                return makeUserListMessage("По увеличению расстояния:", personService.listByRange(message.getFrom().getId().longValue(), null));

            case ID:
                return "Ваш telegramID: " + message.getFrom().getId();
                
            case STICKER:
                return "StickerID: " + parsedCommand.getText();
        }
        return "";
    }

    private String makeUserListMessage(String header, List<Person> persons) {

        if (persons == null || persons.isEmpty()) {
            return "Ничего не найдено.\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(header).append("\n\n");
        for (Person p : persons) {
            sb.append(p.getShortDescription()).append("\n");
        }
        return sb.toString();
    }

    private SendMessage getMessageHelp(String chatID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);
        sendMessage.enableMarkdown(true);

        StringBuilder text = new StringBuilder();
        text.append("*This is help message*").append(END_LINE).append(END_LINE);
        text.append("[/start](/start) - show start message").append(END_LINE);
        text.append("[/help](/help) - show help message").append(END_LINE);
        text.append("/*from* _место_ - сохранить место откуда ты").append(END_LINE);
        text.append("/*list* - показать все записи откуда кто").append(END_LINE);
        text.append("/*info* _имя или место_ - поискать по имени или по месту").append(END_LINE);
        text.append("/*byrange* - список участников чата сортированных по расстоянию до вас (сперва близкие)").append(END_LINE);

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
