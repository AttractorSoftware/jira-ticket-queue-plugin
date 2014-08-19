package it.ru.mail.jira.plugins.disposition;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ReindexTest {
    private static final String CI_URL="http://192.168.0.210:2990/jira/secure/Dashboard.jspa";
    private static final String LOCAL_URL="http://localhost:2990/jira/secure/Dashboard.jspa";

    private static Browser browser = new Browser(LOCAL_URL);

    @BeforeClass
    public static void LogIn(){
        browser.manage().window().maximize();
        browser.loginWithUsernameAndPassword("admin", "admin");
    }

    @AfterClass
    public static void LogOut(){
        browser.logOut();
        browser.quit();
    }

    @Test
    public void testReindexOldIssues(){
        browser.removeIssuesAndField();
        browser.createIssues(10);
        browser.addQueueField();
        assertTrue(browser.issuesEnumerationCorrect());
    }

    @Test
    public void testClosedIssuesDoNotGetNumber(){
        browser.removeIssuesAndField();
        browser.createIssues(10);
        browser.closeFewIssues();
        browser.addQueueField();
        assertTrue(browser.issuesEnumerationCorrect());
    }
}
