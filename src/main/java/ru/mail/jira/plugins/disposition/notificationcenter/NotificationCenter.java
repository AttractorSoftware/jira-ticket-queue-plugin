package ru.mail.jira.plugins.disposition.notificationcenter;

import com.atlassian.crowd.embedded.api.Group;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.watchers.WatcherManager;
import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.util.UserUtil;
import com.atlassian.mail.queue.MailQueue;
import org.apache.velocity.exception.VelocityException;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationCenter {
    private WatcherManager watcherManager = ComponentAccessor.getWatcherManager();
    private GroupManager groupManager = ComponentAccessor.getGroupManager();
    private UserUtil userUtil = ComponentAccessor.getUserUtil();
    private MailQueue mailQueue = ComponentAccessor.getMailQueue();
    private List<Message> messages = new CopyOnWriteArrayList<Message>();
    private static NotificationCenter instance;

    public static NotificationCenter getInstance() {
        if(instance == null) {
            instance = new NotificationCenter();
        }
        return instance;
    }

    public void createUpdatedValueMessages(IssueChange issueChange) {
        Group group = groupManager.getGroup(issueChange.getQueueName());
        Collection<String> userNamesInGroup = groupManager.getUserNamesInGroup(group);
        List<String> usernames = watcherManager.getCurrentWatcherUsernames(issueChange.getIssue());
        usernames = mergeUsernames(usernames, userNamesInGroup);
        for(String username:usernames) {
            User user = userUtil.getUser(username);
//            SortedSet<Group> userGroups = userUtil.getGroupsForUser(username);
//            if(userGroups.contains(group)) {
                createNewMessageForChangedDisposition(issueChange, user);
//            }
        }

    }

    private List<String> mergeUsernames(List<String> usernames, Collection<String> userNamesInGroup) {
        Set<String> noDuplicateUsernames = new HashSet<String>(usernames);
        noDuplicateUsernames.addAll(userNamesInGroup);
        return new ArrayList<String>(noDuplicateUsernames);
    }


    private void createNewMessageForChangedDisposition(IssueChange issueChange, User recipient) {
        if(!isMessageForRecipientInList(recipient)) {
            Message message = new Message();
            message.setRecipient(recipient);
            message.addIssueChange(issueChange);
            messages.add(message);
        }
        else {
            Message oldMessage = getMessageForRecipient(recipient);
            oldMessage.addIssueChange(issueChange);
        }
    }

    private boolean isMessageForRecipientInList(User recipient) {
        for(Message message:messages) {
            if(message.getRecipient().equals(recipient))
                return true;
        }
        return false;
    }

    private Message getMessageForRecipient(User recipient) {
        for(Message message:messages) {
            if(message.getRecipient().equals(recipient))
                return message;
        }
        return null;
    }

    public void addMessagesToMailQueue() throws VelocityException {
        for (Message message : messages) {
            mailQueue.addItem(message.getMailQueueItem());
            messages.remove(message);
        }

    }


}
