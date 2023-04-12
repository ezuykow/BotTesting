package com.example.bottesting.ListViewer;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author ezuykow
 */
@Component
public class InlineListViewer {

    private static final int PAGE_SIZE = 3;

    private final TelegramBot bot;

    private LinkedList<String> list = createList();

    public InlineListViewer(TelegramBot bot) {
        this.bot = bot;
    }

    public void start(long chatId) {
        int pageSize = Math.min(list.size(), PAGE_SIZE);
        ListPage listPage = ListPage.createListPage(list, pageSize, 0);
        showMapPartWithInline(chatId, listPage);
    }

    public void switchPageToNext(Update update) {
        String data = update.callbackQuery().data();
        int lastShowedIndex = Integer.parseInt(data.substring(data.lastIndexOf(" ") + 1));

        int pageSize = Math.min(PAGE_SIZE, list.size() - (lastShowedIndex + 1));
        ListPage listPage = ListPage.createListPage(list, pageSize, lastShowedIndex + 1);
        System.out.println(listPage.getText());
        editView(update, listPage);
    }

    public void switchPageToPrevious(Update update) {
        String data = update.callbackQuery().data();
        int firstShowedIndex = Integer.parseInt(data.substring(data.lastIndexOf(" ") + 1));

        ListPage listPage = ListPage.createListPage(list, PAGE_SIZE, firstShowedIndex - PAGE_SIZE);
        editView(update, listPage);
    }

    public void deleteMessage(Update update) {
        DeleteMessage msg = new DeleteMessage(update.callbackQuery().message().chat().id(),
                update.callbackQuery().message().messageId());
        bot.execute(msg);
    }

    public void viewChoosenElement(Update update) {
        String data = update.callbackQuery().data();
        int index = Integer.parseInt(data.substring(data.lastIndexOf(" ") + 1));
        ElementView view = ElementView.createElementView(list, index - 1);
        showElementView(update, view);
    }

    private LinkedList<String> createList() {
        LinkedList<String> list = new LinkedList<>();

        list.add("first");
        list.add("second");
        list.add("third");
        list.add("fourth");
        list.add("fifth");
        list.add("sixth");
        list.add("бобер");
        list.add("съел");
        list.add("много");
        list.add("сливы...");
        list.add("пипец бобру(");

        return list;
    }

    private void showMapPartWithInline(long chatId, ListPage listPage) {
        SendMessage msg = new SendMessage(chatId, listPage.getText())
                .replyMarkup(listPage.getKeyboard());
        bot.execute(msg);
    }

    private void editView(Update update, ListPage listPage) {
        EditMessageText msg = new EditMessageText(update.callbackQuery().message().chat().id(),
                update.callbackQuery().message().messageId(), listPage.getText())
                .replyMarkup(listPage.getKeyboard());
        bot.execute(msg);
    }

    private void showElementView(Update update, ElementView view) {
        EditMessageText msg = new EditMessageText(update.callbackQuery().message().chat().id(),
                update.callbackQuery().message().messageId(), view.getText())
                .replyMarkup(view.getKeyboard());
        bot.execute(msg);
    }
}
