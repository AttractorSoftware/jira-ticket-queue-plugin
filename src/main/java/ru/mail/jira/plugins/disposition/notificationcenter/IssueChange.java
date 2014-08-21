package ru.mail.jira.plugins.disposition.notificationcenter;

import com.atlassian.jira.issue.Issue;

public class IssueChange {
    Issue issue;
    Double prevValue;
    Double newValue;
    String queueName;

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setPrevValue(Double prevValue) {
        this.prevValue = prevValue;
    }

    public Double getPrevValue() {
        return prevValue;
    }

    public void setNewValue(Double newValue) {
        this.newValue = newValue;
    }

    public Double getNewValue() {
        return newValue;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueName() {
        return queueName;
    }
}
