package ru.mail.jira.plugins.disposition.notificationcenter;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mail.Email;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.util.VelocityParamFactory;
import com.atlassian.jira.web.bean.I18nBean;
import com.atlassian.mail.queue.MailQueueItem;
import com.atlassian.mail.queue.SingleMailQueueItem;
import com.atlassian.velocity.VelocityManager;
import org.apache.velocity.exception.VelocityException;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class Message {
    public static final String I18N = "i18n";
    public static final String EMAIL_MIME_TYPE = "text/html";
    public static final String SUBJECT = "Changing the position your issues in the queue";
    public static final String TEMPLATE_PATH = "/ru/mail/jira/plugins/disposition/templates/email/";
    public static final String TEMPLATE_NAME = "email-template.vm";
    public static final String ENCODING = "UTF-8";
    public static final String QUEUE_MANAGER_GROUP_NAME = "queueManager";

    private static Comparator<IssueChange> comparator = new Comparator<IssueChange>(){
        public int compare(IssueChange ic1, IssueChange ic2){
            return (int)(ic1.getNewValue() - ic2.getNewValue());
        }
    };

    public Comparator<IssueChange> timestampComparator = new Comparator<IssueChange>() {
        @Override
        public int compare(IssueChange o1, IssueChange o2) {
            return o1.getTimestamp().compareTo(o2.getTimestamp());
        }
    };

    private GroupManager groupManager = ComponentAccessor.getGroupManager();
    private Group group = groupManager.getGroup(QUEUE_MANAGER_GROUP_NAME);

    private User recipient;
    private List<IssueChange> issueChanges = new CopyOnWriteArrayList<IssueChange>();

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
        collapseIssueChanges();
        Collection<User> queueManagers = groupManager.getUsersInGroup(group.getName());
        context.putAll(defaultVelocityParams);
        context.put("queues", groupChangesByQueue());
        context.put("queueManagers", queueManagers);
        context.put(I18N, i18n);
        return velocityManager.getEncodedBody(TEMPLATE_PATH, TEMPLATE_NAME, ENCODING, context);
    }

    private void collapseIssueChanges() {
        List<IssueChange> issueChangeList = new CopyOnWriteArrayList<IssueChange>(issueChanges);
        List<IssueChange> collasedIssueChanges = new ArrayList<IssueChange>();

        for(IssueChange issueChange:issueChangeList) {
            List<IssueChange> issueChangesForOneIssue = getIssueChangesForIssue(issueChangeList, issueChange.getIssue());

            Collections.sort(issueChangesForOneIssue, timestampComparator);

            if (!issueChangesForOneIssue.isEmpty()) {
                createAndAppendCollapsedIssueChanges(collasedIssueChanges, issueChangesForOneIssue);
            }
        }
        issueChanges = collasedIssueChanges;
    }

    private void createAndAppendCollapsedIssueChanges(List<IssueChange> collapsedIssueChanges, List<IssueChange> issueChangesForOneIssue) {
        IssueChange collapsedIssueChange = new IssueChange();
        IssueChange firstIssueChange = issueChangesForOneIssue.get(0);
        IssueChange lastIssueChange = issueChangesForOneIssue.get(issueChangesForOneIssue.size() - 1);

        collapsedIssueChange.setIssue(firstIssueChange.getIssue());
        collapsedIssueChange.setQueueName(firstIssueChange.getQueueName());
        collapsedIssueChange.setPrevValue(firstIssueChange.getPrevValue());
        collapsedIssueChange.setNewValue(lastIssueChange.getNewValue());
        collapsedIssueChange.concatReasons(getAllReasonForIssueChanges(issueChangesForOneIssue));

        collapsedIssueChanges.add(collapsedIssueChange);
    }

    private List<IssueChangeReason> getAllReasonForIssueChanges(List<IssueChange> issueChangesForOneIssue) {
        List<IssueChangeReason> issueChangeReasons = new ArrayList<IssueChangeReason>();
        for(IssueChange issueChange:issueChangesForOneIssue)
            issueChangeReasons.addAll(issueChange.getReason());
        return issueChangeReasons;
    }

    private List<IssueChange> getIssueChangesForIssue(List<IssueChange> issueChanges, Issue issue) {
        List<IssueChange> issueChangeList = new ArrayList<IssueChange>();
        for(IssueChange issueChange:issueChanges) {
            if(issueChange.getIssue().getKey().equals(issue.getKey())) {
                issueChangeList.add(issueChange);
                issueChanges.remove(issueChange);
            }
        }
        return issueChangeList;
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
            Collections.sort(item.getValue(), comparator);
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
