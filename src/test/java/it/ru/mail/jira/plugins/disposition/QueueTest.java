package it.ru.mail.jira.plugins.disposition;

import org.junit.*;

import static org.junit.Assert.assertTrue;

public class QueueTest {
    private static final String CI_URL="http://192.168.0.210:2990/jira/secure/Dashboard.jspa";
    private static final String LOCAL_URL="http://localhost:2990/jira/secure/Dashboard.jspa";

    private static LocalDriver browser = new LocalDriver(LOCAL_URL);

    @BeforeClass
    public static void LogIn(){
        browser.manage().window().maximize();
        browser.loginWithUsernameAndPassword("admin", "admin");
    }

    @AfterClass
    public static void LogOut(){
        //browser.logOut();
    }

    @Test
    public void testDragAndDropTicketUp() throws InterruptedException {
        DragAndDropTicket(10, 1);
    }

    @Test
     public void testDragAndDropTicketDown() throws InterruptedException {
        DragAndDropTicket(2, 7);
    }

    private void DragAndDropTicket(int fromPos, int toPos) throws InterruptedException {
        browser.openIssuesList();
        browser.resetLocalQueue();
        browser.moveTicket(fromPos, toPos);
        //Wait for page to refresh
        Thread.sleep(1000);
        assertTrue(browser.queueCorrect());
    }
}
