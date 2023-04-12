package com.example.bottesting.ListViewer;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.List;

/**
 * @author ezuykow
 */
public class ElementView {

    private String text;
    private InlineKeyboardMarkup keyboard;

    private ElementView() {}

    public static ElementView createElementView(List<String> list, int idx) {
        ElementView view = new ElementView();
        view.text = list.get(idx);
        view.keyboard = createKeyboard(idx);

        return view;
    }

    public String getText() {
        return text;
    }

    public InlineKeyboardMarkup getKeyboard() {
        return keyboard;
    }

    private static InlineKeyboardMarkup createKeyboard(int idx) {
        InlineKeyboardButton but1 = new InlineKeyboardButton("Редактировать")
                .callbackData("Edit element " + idx);
        InlineKeyboardButton but2 = new InlineKeyboardButton("Удалить")
                .callbackData("Delete element " + idx);
        InlineKeyboardButton but3 = new InlineKeyboardButton(Character.toString(0x1F519))
                .callbackData("To listViewer");
        return new InlineKeyboardMarkup(but1, but2, but3);
    }
}
