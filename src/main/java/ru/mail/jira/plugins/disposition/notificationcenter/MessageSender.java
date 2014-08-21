package ru.mail.jira.plugins.disposition.notificationcenter;

import com.atlassian.jira.extension.Startable;
import org.apache.log4j.Logger;
import org.apache.velocity.exception.VelocityException;

import java.util.Timer;
import java.util.TimerTask;

public class MessageSender implements Startable {
    private static final Logger log = Logger.getLogger(MessageSender.class);
    public static final int MINUTE = 1000 * 60;
    public static final String THREAD_NAME = "MessageSenderThread";
    private NotificationCenter notificationCenter = NotificationCenter.getInstance();
    private void send() throws VelocityException {
        notificationCenter.addMessagesToMailQueue();
    }

    @Override
    public void start() throws Exception {
        Timer timer = new Timer(THREAD_NAME, true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    send();
                } catch (VelocityException e) {
                    e.printStackTrace();
                }
            }
        }, 0, MINUTE);
    }


}
