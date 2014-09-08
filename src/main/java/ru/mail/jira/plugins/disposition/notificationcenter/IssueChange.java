package ru.mail.jira.plugins.disposition.notificationcenter;

import com.atlassian.jira.issue.Issue;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class IssueChange {
    Issue issue;
    Double prevValue;
    Double newValue;
    String queueName;
    DateTime timestamp;

    private List<IssueChangeReason> reason = new ArrayList<IssueChangeReason>();

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

    public void addReason(IssueChangeReason reason) {
        this.reason.add(reason);
    }

    public void concatReasons(List<IssueChangeReason> reasons) {
        this.reason.addAll(reasons);
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public List<IssueChangeReason> getReason() {
        return reason;
    }

    public boolean isNewValueMoreThanPrevValue() {
        if (newValue == null || prevValue == null)
            return true; // whatever, either would be false
        return newValue > prevValue;
    }
}
