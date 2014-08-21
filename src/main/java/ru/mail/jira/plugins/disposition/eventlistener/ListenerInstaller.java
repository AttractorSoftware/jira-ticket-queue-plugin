package ru.mail.jira.plugins.disposition.eventlistener;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.extension.Startable;

import java.util.Iterator;
import java.util.Map;

public class ListenerInstaller implements Startable {
    @Override
    public void start() throws Exception {
        installListener();
    }

    private void installListener() {
        if(!isListenerInstalled()) {
            try {
                ComponentAccessor.getListenerManager().createListener("Issue Disposition listener", EventListenerImpl.class);
            }
            catch (Exception e) {

            }
        }
    }

    private boolean isListenerInstalled() {
        Map map = ComponentAccessor.getListenerManager().getListeners();
        for (Object object : map.entrySet()) {
            Map.Entry entry = (Map.Entry) object;
            if ((entry.getValue() instanceof EventListenerImpl))
                return true;
        }
        return false;
    }
}
