package ru.mail.jira.plugins.disposition.notificationcenter;

import org.apache.velocity.exception.VelocityException;
import org.springframework.beans.factory.InitializingBean;

import java.util.Timer;
import java.util.TimerTask;

public class MessageSender implements InitializingBean {
    public static final int MINUTE = 1000 * 60;
    public static final String THREAD_NAME = "MessageSenderThread";
    private NotificationCenter notificationCenter = NotificationCenter.getInstance();
    private void send() throws VelocityException {
        notificationCenter.addMessagesToMailQueue();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Timer timer = new Timer(THREAD_NAME, true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    send();
                } catch (VelocityException ignored) {
                //Ignore messages about template generation errors
                }
            }
        }, 0, MINUTE);
    }
}
