package ru.mail.jira.plugins.disposition.eventlistener;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.jql.builder.JqlOrderByBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.order.SortOrder;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import ru.mail.jira.plugins.disposition.manager.DispositionConfigurationManager;
import ru.mail.jira.plugins.disposition.manager.DispositionManager;
import ru.mail.jira.plugins.disposition.manager.DispositionManagerImpl;
import ru.mail.jira.plugins.disposition.notificationcenter.IssueChangeReason;

import java.util.List;

public class EventListenerImpl extends AbstractIssueEventListener implements InitializingBean, DisposableBean {

    public static final String DISPOSITION_CUSTOM_FIELD_KEY_PART = "disposition";
    EventPublisher eventPublisher;

    private static final Logger log = Logger.getLogger(EventListenerImpl.class);

    private OfBizDelegator delegator = ComponentAccessor.getOfBizDelegator();

    private CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();

    public EventListenerImpl(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    DispositionManager dispositionManager = DispositionManagerImpl.getInstance();


    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        eventPublisher.register(this);
    }

    @EventListener
    public void onIssueCreatedEvent(IssueEvent event) {
        Long eventTypeID = event.getEventTypeId();
        Issue issue = event.getIssue();



        if(eventTypeID.equals(EventType.ISSUE_CREATED_ID)) {
//            putIssueToBottomOfQueues(issue);
        }
        else if(eventTypeID.equals(EventType.ISSUE_REOPENED_ID)) {
//            putIssueToBottomOfQueues(issue);
        }
        else if(eventTypeID.equals(EventType.ISSUE_CLOSED_ID)) {
            shiftUpIssuesAfterThatIssueInQueues(issue);
        }
        else if(eventTypeID.equals(EventType.ISSUE_RESOLVED_ID)) {
            shiftUpIssuesAfterThatIssueInQueues(issue);
        }
        else if(eventTypeID.equals(EventType.ISSUE_ASSIGNED_ID)) {
            notifyAboutStartedProgressOfTicket();
        }
//        else if(eventTypeID.equals(EventType.ISSUE_UPDATED_ID)) {
//            List<CustomField> customFields = getCustomFieldsForIssue(issue);
//            event.getChangeLog();
//            GenericValue changeLog = event.getChangeLog();
//            Long id = changeLog.getLong("id");
//            List<GenericValue> changes = delegator.findByAnd("ChangeItem", MapBuilder.build("group", id));
//            String updated = changes.toString();
//        }
    }

    private List<CustomField> getCustomFieldsForIssue(Issue issue) {
        return customFieldManager.getCustomFieldObjects(issue);
    }

    private void notifyAboutStartedProgressOfTicket() {

    }

    private void shiftUpIssuesAfterThatIssueInQueues(Issue issue) {
        List<CustomField> customFields = getCustomFieldsForIssue(issue);
        for(CustomField customField:customFields) {
            if(customField.getCustomFieldType().getKey().contains(DISPOSITION_CUSTOM_FIELD_KEY_PART)) {
                Issue lastIssue = getLastIssue(issue, customField);
                customField.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(customField), null), new DefaultIssueChangeHolder());
                shiftIssues(issue, customField, lastIssue);
            }
        }

    }

    private void shiftIssues(Issue issue, CustomField customField, Issue lastIssue) {
        DispositionConfigurationManager dispositionConfigurationManager = dispositionManager.getDispositionConfigurationManager();
        String query = dispositionConfigurationManager.getQuery(customField);
        IssueChangeReason reason = new IssueChangeReason();
        reason.setCausalIssue(issue);
        reason.setReasonType(IssueChangeReason.ISSUE_RESOLVED);
        DateTime timestamp = new DateTime();
        dispositionManager.shiftIssuesUp(query, (Double) lastIssue.getCustomFieldValue(customField), customField, issue.getAssigneeUser(), issue, reason, timestamp);
    }

    private Issue getLastIssue(Issue issue, CustomField customField) {
        JqlOrderByBuilder jqlQueryBuilder = JqlQueryBuilder.newBuilder().where().status("Open").or().status("In Progress").or().status("Reopened").endWhere().orderBy().add(customField.getName(), SortOrder.ASC);
        SearchProvider searchProvider = dispositionManager.getSearchProvider();
        Issue lastIssue = null;

        try {
            SearchResults searchResults = searchProvider.search(jqlQueryBuilder.buildQuery(), issue.getAssignee(), PagerFilter.getUnlimitedFilter());
            List<Issue> issues = searchResults.getIssues();
            lastIssue = issues.get(issues.size()-1);
            if(lastIssue.getKey().equals(issue.getKey()))
                lastIssue = issues.get(issues.size()-2);
        } catch (SearchException e) {
            e.printStackTrace();
        }

        return lastIssue;
    }

    private void putIssueToBottomOfQueues(Issue issue) {
        List<CustomField> customFields = getCustomFieldsForIssue(issue);
        for(CustomField customField:customFields) {
            if(customField.getCustomFieldType().getKey().contains(DISPOSITION_CUSTOM_FIELD_KEY_PART)) {
                Issue lastIssue = getLastIssue(issue, customField);
                Double lastIssueValue = (Double) lastIssue.getCustomFieldValue(customField);
                double newValue = lastIssueValue + 1;

                customField.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(customField), newValue), new DefaultIssueChangeHolder());
            }
        }
    }


}
