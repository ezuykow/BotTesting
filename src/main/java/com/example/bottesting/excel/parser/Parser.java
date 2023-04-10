package com.example.bottesting.excel.parser;

import com.example.bottesting.Question;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author ezuykow
 */
@Component
public class Parser {

    Logger logger = LoggerFactory.getLogger(Parser.class);

    private final FileDownloader fileDownloader;
    private final TelegramBot bot;

    private List<Question> newQuestions;

    public Parser(FileDownloader fileDownloader, TelegramBot bot) {
        this.fileDownloader = fileDownloader;
        this.bot = bot;
    }

    public void parse(Document doc, Update update) {
        File file = fileDownloader.getFile(doc.fileId());
        parseFile(file, update);
        logger.info("Downloaded file: {}", file);
    }

    private void parseFile(File file, Update update) {
        try (XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file))) {
            parseSheet(wb.getSheetAt(0));
            checkNewQuestions(update);
        } catch (IOException e) {
            logger.error("Нихуище не фурычит(");
            throw new RuntimeException(e);
        }
    }

    private void parseSheet(Sheet sheet){
        newQuestions = new ArrayList<>();
        StreamSupport.stream(((Iterable<Row>) sheet).spliterator(), false)
                .skip(1)
                .forEach(this::actionWithRow);
    }

    private void actionWithRow(Row row) {
        Optional<Question> question = fillQuestionFromRowCells(row, new Question());
        question.ifPresent(value -> newQuestions.add(value));
    }

    private Optional<Question> fillQuestionFromRowCells(Row row, Question question) {
        int cellNo = 1;
        for (Cell cell : row) {
            if (cell.getCellType() == CellType.STRING) {
                if (cellNo == 1 && cell.getStringCellValue().equalsIgnoreCase("да")) {
                    return Optional.empty();
                }
                fillQuestionFieldFromCell(cellNo, cell, question);
                cellNo++;
            }
        }
        if (question.getQuestion() == null || question.getAnswer() == null) {
            return Optional.empty();
        }
        return Optional.of(question);
    }

    private void fillQuestionFieldFromCell(int cellNo, Cell cell,  Question question) {
        switch (cellNo) {
            case 2 -> question.setQuestion(cell.getStringCellValue());
            case 3 -> question.setAnswerFormat(cell.getStringCellValue());
            case 4 -> question.setAnswer(cell.getStringCellValue());
            case 5 -> question.setMapUrl(cell.getStringCellValue());
            case 6 -> question.setGroup(cell.getStringCellValue());
        }
    }

    private void checkNewQuestions(Update update) {
        SendMessage msg;
        if (newQuestions.isEmpty()) {
            msg = new SendMessage(update.message().chat().id(), "Ни одного вопроса не добавлено");
        } else {
            msg = new SendMessage(update.message().chat().id(), newQuestions.toString());
        }
        bot.execute(msg);
    }
}
