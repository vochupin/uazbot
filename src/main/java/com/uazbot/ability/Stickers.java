package com.uazbot.ability;

import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;

public enum Stickers {
    FUNNY_JIM_CARREY("CAADBQADiQMAAukKyAPZH7wCI2BwFxYE"),
    ;

    String stickerId;

    Stickers(String stickerId) {
        this.stickerId = stickerId;
    }

    public SendSticker getSendSticker(String chatId) {
        if ("".equals(chatId)) throw new IllegalArgumentException("ChatId cant be null");
        SendSticker sendSticker = getSendSticker();
        sendSticker.setChatId(chatId);
        return sendSticker;
    }

    public SendSticker getSendSticker() {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setSticker(new InputFile(stickerId));
        return sendSticker;
    }
}
