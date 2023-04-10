package com.example.bottesting.listeners;

import com.example.bottesting.TaskMapper;
import com.example.bottesting.excel.parser.DocumentManager;
import com.example.bottesting.strings.KeyboardStrings;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BotTestingUpdateListener implements UpdatesListener {

    Logger logger = LoggerFactory.getLogger(BotTestingUpdateListener.class);

    private final TelegramBot telegramBot;
    private final KeyboardStrings keyboardStrings;
    private final JdbcTemplate jdbcTemplate;
    private final DocumentManager docManager;

    @Value("${test}")
    private String testValue;
    @Value("${createStatementPrefix}")
    private String createTablePreparedStatementPrefix;
    @Value("${createStatementPostfix}")
    private String createTablePreparedStatementPostfix;
    @Value("${dropStatementPrefix}")
    private String dropTablePreparedStatementPrefix;
    @Value("${findAllPrefix}")
    private String findAllStatement;

    public BotTestingUpdateListener(TelegramBot telegramBot, KeyboardStrings keyboardStrings, JdbcTemplate jdbcTemplate, DocumentManager docManager) {
        this.telegramBot = telegramBot;
        this.keyboardStrings = keyboardStrings;
        this.jdbcTemplate = jdbcTemplate;
        this.docManager = docManager;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(this::doAction);
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void doAction(Update update) {
        logger.info("Работаю над командой: {}", update);

        Optional<String> msgText = tryToGetText(update);
        msgText.ifPresent(s -> switchTextAction(s, update));

        Optional<String> updateQueryText = tryToGetUpdateQueryText(update);
        updateQueryText.ifPresent(s -> switchUpdateQueryAction(s, update));

        Optional<Document> updateDocument = tryToGetDocument(update);
        updateDocument.ifPresent(d -> docManager.manageDoc(d, update));
    }

    private void switchTextAction(String text, Update update) {


        logger.info("Текст апдейта: {}", text);
        switch (text) {
            case "/InlineKeyboard" -> inlineKeyboard(update);
            case "/ReplyKeyboard" -> replyKeyboard(update);
            case "КнопкаRKM1", "КнопкаRKM2", "КнопкаRKM3" -> removeReplyKeyboard(update);
            case "Create table test_table" -> createTable();
            case "Drop table test_table" -> dropTable();
            case "Show all" -> showAll();
            case "format" -> format();
            case "userinfo" -> showUsername(update);
            default -> defaultAction(update);
        }
    }

    private void showUsername(Update update) {
        User user = update.message().from();
        System.out.printf("id: %d, username: %s, firstname: %s, lastname: %s\n",
                user.id(), user.username(), user.firstName(), user.lastName());
    }

    private void format() {
        System.out.println(String.format("SELECT * FROM %s WHERE %s = %d", "tableName", "idColumnName", 123L));
    }

    private void showAll() {
        System.out.println(jdbcTemplate.query(findAllStatement  + "test_table", new TaskMapper()));
    }

    private void createTable() {
        String createTablePreparedStatement =
                createTablePreparedStatementPrefix + "test_table" + createTablePreparedStatementPostfix;
        jdbcTemplate.update(createTablePreparedStatement);
    }

    private void dropTable() {
        jdbcTemplate.update(dropTablePreparedStatementPrefix + "test_table");
    }

    private void defaultAction(Update update) {
        telegramBot.execute(new SendMessage(update.message().chat().id(), keyboardStrings.reply));
        System.out.print(testValue);
        System.out.println("<- cursor is here");
    }

    private void switchUpdateQueryAction(String text, Update update) {
        switch (text) {
            case "B1_1" ->
                    logger.info("B1_1");
//                    telegramBot.execute(new SendMessage(update.message().chat().id(), "B1_1"));
            case "B1_2" -> telegramBot.execute(new SendMessage(update.message().chat().id(), "B1_2"));
            case "B2_1" -> telegramBot.execute(new SendMessage(update.message().chat().id(), "B2_1"));
        }
    }

    private void inlineKeyboard(Update update) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton button1_1 = new InlineKeyboardButton("<-")
                .callbackData("Нажата B1_1");
        InlineKeyboardButton button1_2 = new InlineKeyboardButton("->")
                .callbackData("Нажата B1_2");
        InlineKeyboardButton button2_1 = new InlineKeyboardButton("Удалить вопрос")
                .callbackData("Нажата B2_1");

        inlineKeyboardMarkup.addRow(button1_1, button1_2);
        inlineKeyboardMarkup.addRow(button2_1);

        SendMessage msg = new SendMessage(update.message().chat().id(), "страница 1/5\n 1. Кто проживает на дне океана?\n .....")
                .replyMarkup(inlineKeyboardMarkup);
        SendResponse response = telegramBot.execute(msg);
    }

    private void replyKeyboard(Update update) {
        KeyboardButton brkm1 = new KeyboardButton("КнопкаRKM1");
        KeyboardButton brkm2 = new KeyboardButton("КнопкаRKM2");
        KeyboardButton brkm3 = new KeyboardButton("КнопкаRKM3");
        KeyboardButton[] buttonsRow1 = new KeyboardButton[]{brkm1, brkm2};
        KeyboardButton[]  buttonsRow2 = new KeyboardButton[]{brkm3};
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(buttonsRow1, buttonsRow2)
                .oneTimeKeyboard(true);


        SendMessage request = new SendMessage(update.message().chat().id(), "ReplyKeyboard")
                .replyMarkup(keyboardMarkup);
        SendResponse response = telegramBot.execute(request);
    }

    private void removeReplyKeyboard(Update update) {
        SendMessage msg = new SendMessage(update.message().chat().id(), "Принято!")
                .replyMarkup(new ReplyKeyboardRemove());
        telegramBot.execute(msg);
    }

    private Optional<String> tryToGetText(Update update) {
        try {
            return Optional.of(update.message().text());
        } catch (NullPointerException e) {
            logger.info("Update не содержит текст");
            return Optional.empty();
        }
    }

    private Optional<String> tryToGetUpdateQueryText(Update update) {
        try {
            return Optional.of(update.callbackQuery().data());
        } catch (NullPointerException e) {
            logger.info("Update не содержит callbackQuery");
            return Optional.empty();
        }
    }

    private Optional<Document> tryToGetDocument(Update update) {
        try {
            return Optional.of(update.message().document());
        } catch (NullPointerException e) {
            logger.info("Update не содержит document");
            return Optional.empty();
        }
    }
}
