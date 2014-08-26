package ru.mail.jira.plugins.disposition.notificationcenter;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mail.Email;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.jira.util.VelocityParamFactory;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.mail.queue.SingleMailQueueItem;
import com.atlassian.velocity.VelocityManager;
import org.apache.velocity.exception.VelocityException;

import java.util.*;


public class Message {
    public static final String I18N = "i18n";
    public static final String EMAIL_MIME_TYPE = "text/html";
    public static final String SUBJECT = "Changing the position your issues in the queue";
    public static final String TEMPLATE_PATH = "/ru/mail/jira/plugins/disposition/templates/email/";
    public static final String TEMPLATE_NAME = "email-template.vm";
    public static final String ENCODING = "UTF-8";
    public static final String QUEUE_MANAGER_GROUP_NAME = "queueManager";

    private GroupManager groupManager = ComponentAccessor.getGroupManager();
    private Group group = groupManager.getGroup(QUEUE_MANAGER_GROUP_NAME);

    private User recipient;
    private List<IssueChange> issueChanges = new ArrayList<IssueChange>();

    VelocityManager velocityManager = ComponentAccessor.getVelocityManager();
    private JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext();
    private VelocityParamFactory velocityParamFactory = ComponentAccessor.getVelocityParamFactory();
    private Map<String, Object> defaultVelocityParams = velocityParamFactory.getDefaultVelocityParams(jiraAuthenticationContext);;
    private I18nBean i18n = new I18nBean();

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public User getRecipient() {
        return recipient;
    }


    public void addIssueChange(IssueChange issueChange) {
        issueChanges.add(issueChange);
    }

    private String renderMessage() throws VelocityException {
        Map<String, Object> context = new HashMap<String, Object>();
        Collection<User> queueManagers = groupManager.getUsersInGroup(group.getName());
        context.putAll(defaultVelocityParams);
        context.put("queues", groupChangesByQueue());
        context.put("queueManagers", queueManagers);
        context.put(I18N, i18n);
        return velocityManager.getEncodedBody(TEMPLATE_PATH, TEMPLATE_NAME, ENCODING, context);
    }

    private Map<String, List<IssueChange>> groupChangesByQueue() {
        Map <String, List<IssueChange>> issueChangesGroupedByQueue = new HashMap<String, List<IssueChange>>();
        for (IssueChange issueChange : issueChanges) {
            String queueName = issueChange.getQueueName();
            if (!issueChangesGroupedByQueue.containsKey(queueName)) issueChangesGroupedByQueue.put(queueName, new ArrayList<IssueChange>());
            issueChangesGroupedByQueue.get(queueName).add(issueChange);
        }
        for(Map.Entry <String, List<IssueChange>> item : issueChangesGroupedByQueue.entrySet())
        {
            Collections.reverse(item.getValue());
        }
        return issueChangesGroupedByQueue;
    }

    public MailQueueItem getMailQueueItem() throws VelocityException {
        Email email = new Email(recipient.getEmailAddress());
        email.setSubject(SUBJECT);
        email.setBody(renderMessage());
        email.setMimeType(EMAIL_MIME_TYPE);
        return new SingleMailQueueItem(email);
    }
}
