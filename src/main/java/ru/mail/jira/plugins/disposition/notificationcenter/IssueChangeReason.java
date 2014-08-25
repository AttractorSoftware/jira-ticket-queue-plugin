package ru.mail.jira.plugins.disposition.notificationcenter;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.Issue;

public class IssueChangeReason {
    public static final int MANUALY_CHANED = 1;
    public static final int INSERTED_ABOVE = 2;
    public static final int REMOVED_ABOVE = 3;
    public static final int ISSUE_RESOLVED = 4;

    private int reasonType;

    private Issue causalIssue;

    private User user;

    public void setCausalIssue(Issue causalIssue) {
        this.causalIssue = causalIssue;
    }

    public Issue getCausalIssue() {
        return causalIssue;
    }

    public void setReasonType(int reasonType) {
        this.reasonType = reasonType;
    }

    public int getReasonType() {
        return reasonType;
    }

    public String getUserDisplayName(){
        return user.getDisplayName();
    }

    public void setUser(User user) {
        this.user = user;
    }
}
