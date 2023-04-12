package com.example.bottesting.ListViewer;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.Keyboard;

import java.util.List;

/**
 * @author ezuykow
 */
public class ListPage {

    private List<String> allElements;
    private int pageSize;
    private int startElementIdx;
    private int lastElementIdx;
    private String text;
    private InlineKeyboardMarkup keyboard;
    private boolean leftArrowNeeded;
    private boolean rightArrowNeeded;

    private ListPage() {

    }

    public static ListPage createListPage(List<String> list, int pageSize, int startElementIdx) {
        ListPage page = new ListPage();
        page.allElements = list;
        page.pageSize = pageSize;
        page.startElementIdx = startElementIdx;
        page.lastElementIdx = Math.min((startElementIdx + pageSize - 1), list.size() - 1);

        page.createText();
        page.checkArrowsNeed();
        page.createKeyboard();

        return page;
    }

    public String getText() {
        return text;
    }

    public InlineKeyboardMarkup getKeyboard() {
        return keyboard;
    }

    private void createText() {
        StringBuilder sb = new StringBuilder();
        for (int i = startElementIdx; i <= lastElementIdx; i++) {
            sb.append(i + 1).append(". ").append(allElements.get(i)).append("\n");
        }

        text = sb.toString();
    }

    private void checkArrowsNeed() {
        leftArrowNeeded = startElementIdx != 0;
        rightArrowNeeded = lastElementIdx != allElements.size() - 1;
    }

    private void createKeyboard() {
        keyboard = new InlineKeyboardMarkup(createButtons());
    }

    private InlineKeyboardButton[] createButtons() {
        InlineKeyboardButton[] buttons = new InlineKeyboardButton[pageSize + 2];
        int currentButtonIdx = 0;

        buttons[currentButtonIdx++] = leftArrowNeeded
                ? new InlineKeyboardButton("\u25C0")
                .callbackData("Switch page to previous. First element index: " + startElementIdx)
                : new InlineKeyboardButton("\u274C")
                .callbackData("Delete message");

        for (int i = startElementIdx; i <= lastElementIdx; i++) {
            buttons[currentButtonIdx++] = new InlineKeyboardButton(String.valueOf(i + 1))
                    .callbackData("Taken index: " + (i + 1));
        }

        buttons[currentButtonIdx] = rightArrowNeeded
                ? new InlineKeyboardButton("\u25B6")
                .callbackData("Switch page to next. Last element index: " + lastElementIdx)
                : new InlineKeyboardButton("\u274C")
                .callbackData("Delete message");

        return buttons;
    }

}
