package com.example.bottesting;

import java.util.Date;

/**
 * @author ezuykow
 */
public class Question {

    private int questionId;
    private String question;
    private String answerFormat;
    private String answer;
    private String mapUrl;
    private Date lastUsage;
    private String group;

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerFormat() {
        return answerFormat;
    }

    public void setAnswerFormat(String answerFormat) {
        this.answerFormat = answerFormat;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public Date getLastUsage() {
        return lastUsage;
    }

    public void setLastUsage(Date lastUsage) {
        this.lastUsage = lastUsage;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", question='" + question + '\'' +
                ", answerFormat='" + answerFormat + '\'' +
                ", answer='" + answer + '\'' +
                ", mapUrl='" + mapUrl + '\'' +
                ", lastUsage=" + lastUsage +
                ", group='" + group + '\'' +
                '}';
    }
}
