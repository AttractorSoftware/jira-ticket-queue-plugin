package ru.mail.jira.plugins.disposition.eventlistener;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.AbstractIssueEventListener;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchProvider;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.jql.parser.DefaultJqlQueryParser;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.query.Query;
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

    private CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();

    public EventListenerImpl(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    DispositionManager dispositionManager = DispositionManagerImpl.getInstance();
    DispositionConfigurationManager dispositionConfigurationManager = dispositionManager.getDispositionConfigurationManager();
    JqlQueryParser jqlQueryParser = new DefaultJqlQueryParser();


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

        if(eventTypeID.equals(EventType.ISSUE_CLOSED_ID)) {
            shiftUpIssuesAfterThatIssueInQueues(issue, event.getUser());
        }

    }

    private void shiftUpIssuesAfterThatIssueInQueues(Issue issue, User user) {
        List<CustomField> customFields = getCustomFieldsForIssue(issue);
        for(CustomField customField:customFields) {
            if(customField.getCustomFieldType().getKey().contains(DISPOSITION_CUSTOM_FIELD_KEY_PART)) {
                Issue lastIssue = getLastIssue(issue, customField, user);

                if(customField.getValue(issue) != null)
                    if(!lastIssue.getKey().equals(issue.getKey()))
                        shiftIssues(issue, customField, lastIssue, user);

                customField.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(customField), null), new DefaultIssueChangeHolder());
            }
        }
    }

    private List<CustomField> getCustomFieldsForIssue(Issue issue) {
        return customFieldManager.getCustomFieldObjects(issue);
    }

    private void shiftIssues(Issue issue, CustomField customField, Issue lastIssue, User user) {
        String query = dispositionConfigurationManager.getQuery(customField);

        IssueChangeReason reason = new IssueChangeReason();
        reason.setCausalIssue(issue);
        reason.setReasonType(IssueChangeReason.ISSUE_RESOLVED);

        DateTime timestamp = new DateTime();
        Double startValue = (Double) lastIssue.getCustomFieldValue(customField);

        dispositionManager.shiftIssuesUp(query, startValue, customField, user, issue, reason, timestamp);
    }

    private Issue getLastIssue(Issue issue, CustomField customField, User user) {
        String query = dispositionConfigurationManager.getQuery(customField);
        Issue lastIssue = null;

        try {
            SearchResults searchResults = getSearchResults(issue, user, query);
            List<Issue> issues = searchResults.getIssues();

            int shiftValue = getNotQueuedIssuesShiftValue(customField, issues);

            lastIssue = issues.get(issues.size() - shiftValue);
        } catch (JqlParseException e) {
            e.printStackTrace();
        } catch (SearchException e) {
            e.printStackTrace();
        }

        return lastIssue;
    }

    private int getNotQueuedIssuesShiftValue(CustomField customField, List<Issue> issues) {
        int shiftValue = 1;
        for(Issue issue:issues){
            if(issue.getCustomFieldValue(customField) == null) {
                shiftValue++;
            }
        }
        return shiftValue;
    }

    private SearchResults getSearchResults(Issue issue, User user, String query) throws JqlParseException, SearchException {
        Query jql = jqlQueryParser.parseQuery(query);
        JqlQueryBuilder jqlQueryBuilder = JqlQueryBuilder.newBuilder(jql);

        SearchProvider searchProvider = dispositionManager.getSearchProvider();

        return searchProvider.search(jqlQueryBuilder.buildQuery(), user, PagerFilter.getUnlimitedFilter());
    }


}
