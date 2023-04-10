package com.example.bottesting.excel.parser;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author ezuykow
 */
@Component
public class DocumentManager {

    Logger logger = LoggerFactory.getLogger(DocumentManager.class);

    private final TelegramBot bot;
    private final Parser parser;

    public DocumentManager(TelegramBot bot, Parser parser) {
        this.bot = bot;
        this.parser = parser;
    }

    public void manageDoc(Document doc, Update update) {
        logger.info("Working with Document");

        if (docIsExcel(doc)) {
            parser.parse(doc, update);
        } else {
            sendMsg("This document is not Excel!", update.message().chat().id());
        }
    }

    private boolean docIsExcel(Document doc) {
        return doc.mimeType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    private void sendMsg(String text, long chatId) {
        SendMessage msg = new SendMessage(chatId, text);
        bot.execute(msg);
    }
}
