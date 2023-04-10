package com.example.bottesting.strings;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KeyboardStrings {

    @Value("${newgame.reply}")
    public String reply;
}
