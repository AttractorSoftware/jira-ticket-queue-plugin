package it.ru.mail.jira.plugins.disposition;

import org.junit.*;

import static org.junit.Assert.assertTrue;

public class QueueTest {
    private static Browser browser = new Browser();

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
    public void testMoveIssueUp() throws InterruptedException {
        moveIssue(10, 1);
    }

    @Test
    public void testMoveIssueDown() throws InterruptedException {
        moveIssue(2, 7);
    }

    private void moveIssue(int fromPos, int toPos) throws InterruptedException {
        browser.openIssuesList();
        browser.resetLocalQueue();
        browser.moveIssue(fromPos, toPos);
        //Wait for page to refresh
        Thread.sleep(1000);
        assertTrue(browser.queueCorrect());
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

    @Test
    public void testCreateIssueWithExistingNumber(){

    }
}
