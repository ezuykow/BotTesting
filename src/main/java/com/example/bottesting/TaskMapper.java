package com.example.bottesting;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TaskMapper implements RowMapper<Task> {

    private String taskIdColumnName = "task_id";
    private String questionIdColumnName = "question_id";
    private String performedTeamNameColumnName = "performed_team_name";

    @Override
    public Task mapRow(ResultSet rs, int rowNum) throws SQLException {

        Task task = new Task();
        task.setTaskId(rs.getInt(taskIdColumnName));
        task.setQuestionId(rs.getLong(questionIdColumnName));
        task.setPerformedTeamName(rs.getString(performedTeamNameColumnName));

        return task;
    }
}
