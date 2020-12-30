package com.uazbot.handler;

import com.uazbot.bot.Bot;
import com.uazbot.command.Command;
import com.uazbot.command.ParsedCommand;
import com.uazbot.entity.Person;
import com.uazbot.restservice.AppConfig;
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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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

        switch (command) {
            case START:
                bot.sendMessage(getMessageStart(chatId));
                break;

            case HELP:
                bot.sendMessage(getMessageHelp(chatId));
                break;

            case FROM:
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
                person.setUserPlace(parsedCommand.getText());

                person.setText(Stream.of(person.getFirstName(), person.getLastName(), person.getUserName())
                .filter(Objects::nonNull)
                    .filter(Predicate.not(String::isBlank))
                    .collect(Collectors.joining(" ")));

                Address address = nominatimService.getAddress(parsedCommand.getText());

                if (address != null) {
                    log.debug("Адрес получен: " + address.getDisplayName());

                    person.setOsmPlaceName(address.getDisplayName());

                    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                    Point point = geometryFactory.createPoint(new Coordinate(address.getLongitude(), address.getLatitude()));
                    person.setOsmMapPoint(point);
                }

                personService.createOrUpdatePerson(person);

                return "Запись по персоне создана(изменена): \n" + person.getLongDescription();

            case LIST:
                return makeUserListString("Полный список участников:", personService.list());

            case BYNAME:
                if (parsedCommand.getText() == null || parsedCommand.getText().trim().isEmpty()) {
                    return "Должен быть параметр";
                }

                return makeUserListString("Найдено по имени:", personService.findByName(parsedCommand.getText()));

            case BYPLACE:
                if (parsedCommand.getText() == null || parsedCommand.getText().trim().isEmpty()) {
                    return "Должен быть параметр";
                }

                return makeUserListString("Найдено по адресу::", personService.findByAddress(parsedCommand.getText()));

            case BYRANGE:
                return makeUserListString("По увеличению расстояния:", personService.listByRange(update.getMessage().getFrom().getId().longValue(), null));

            case ID:
                return "Ваш telegramID: " + update.getMessage().getFrom().getId();
                
            case STICKER:
                return "StickerID: " + parsedCommand.getText();
        }
        return "";
    }

    private String makeUserListString(String header, List<Person> persons) {

        if (persons == null || persons.isEmpty()) {
            return "Ничего не найдено.\n";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(header).append("\n");
        for (Person p : persons) {
            sb.append("  ").append(p.getShortDescription()).append("\n");
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
        text.append("/*byname* _имя_ - поискать по имени").append(END_LINE);
        text.append("/*byplace* _место_ - поискать по имени места из OSM").append(END_LINE);
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
