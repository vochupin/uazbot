package com.uazbot;

import com.uazbot.command.Command;
import com.uazbot.command.ParsedCommand;
import com.uazbot.command.Parser;
import com.uazbot.restservice.AppConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class ParserTest {

    @Autowired
    AppConfig appConfig;

    @Autowired
    Parser parser;

    @Test
    public void getParsedCommand_None() {
        String text = "just text";
        ParsedCommand parsedCommandAndText = parser.getParsedCommand(text);
        assertEquals(Command.NONE, parsedCommandAndText.getCommand());
        assertEquals(text, parsedCommandAndText.getText());
    }

    @Test
    public void getParsedCommand_NotForMe() {
        String text = "/test@another_Bot just text";
        ParsedCommand parsedCommandAndText = parser.getParsedCommand(text);
        assertEquals(Command.NOTFORME, parsedCommandAndText.getCommand());
    }

    @Test
    public void getParsedCommand_NoneButForMe() {
        String text = "/test@" + appConfig.getBotName() + " just text";
        ParsedCommand parsedCommandAndText = parser.getParsedCommand(text);
        assertEquals(Command.NONE, parsedCommandAndText.getCommand());
        assertEquals("just text", parsedCommandAndText.getText());
    }

}