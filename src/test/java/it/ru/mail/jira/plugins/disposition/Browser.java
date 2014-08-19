package it.ru.mail.jira.plugins.disposition;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Browser extends FirefoxDriver implements WebDriver{
    private static final int QUEUE_START = 1;
    private static final int QUEUE_END = 10;
    private List<String> queue = new ArrayList<String>();

    public Browser(String url){
        super();
        get(url);
    }

    public void loginWithUsernameAndPassword(String username, String password){
        WebElement frame = findElement(By.xpath("//iframe[@id=\"gadget-0\"]"));
        switchTo().frame(frame);
        WebElement login_field = findElement(By.xpath("//input[@id=\"login-form-username\"]"));
        WebElement password_field = findElement(By.xpath("//input[@id=\"login-form-password\"]"));
        WebElement login_button = findElement(By.xpath("//input[@id=\"login\"]"));
        login_field.sendKeys(username);
        password_field.sendKeys(password);
        login_button.click();
    }

    public void logOut(){
        WebElement userMenu = findElement(By.xpath("//nav[@class=\"global\"]//a/span"));
        userMenu.click();
        WebElement logOutButton = findElement(By.xpath("//a[@id=\"log_out\"]"));
        logOutButton.click();
        navigate().to("http://192.168.0.210:2990/jira/secure/Dashboard.jspa");
    }

    public void openIssuesList(){
        WebElement issuesLink = waitForElement("//a[@id=\"find_link\"]", 5);
        issuesLink.click();
        WebElement searchButton = findElement(By.xpath("//input[@id=\"issue-filter-submit\"]"));
        searchButton.click();
        WebElement sortByQueueButton = findElement(By.xpath("//span[@title=\"Sort By Queue Position\"]"));
        sortByQueueButton.click();
    }

    public void resetLocalQueue() {
        queue.clear();
        createLocalQueue();
    }

    public void moveIssue(int fromPos, int toPos){
        moveIssueInQueue(fromPos, toPos);
        moveIssueLocally(fromPos, toPos);
    }

    public boolean queueCorrect(){
        for(int i=QUEUE_START; i<=QUEUE_END; i++)
        {
            if(getTicketIdByQueuePosition(i).equals(queue.get(i-1))){
                return false;
            }
        }
        return true;
    }

    public void createLocalQueue(){
        String ticketId;
        for(int i=QUEUE_START; i<=QUEUE_END; i++)
        {
            ticketId = getTicketIdByQueuePosition(i);
            queue.add(i-1, ticketId);
        }
    }

    public void moveIssueLocally(int fromPos, int toPos){
        String movedTicketId = queue.get(fromPos-1);
        if(fromPos<toPos){
            for(int i=fromPos-1; i<toPos-1; i++)
            {
                queue.set(i, queue.get(i + 1));
            }
        }
        else
        {
            for(int i=fromPos-1; i>toPos-1; i--)
            {
                queue.set(i, queue.get(i - 1));
            }
        }
        queue.set(toPos-1, movedTicketId);
    }

    public void moveIssueInQueue(int fromPos, int toPos){
        int yOffset = fromPos < toPos ? 5 : -5;
        WebElement fromRow = getRowByQueuePosition(fromPos);
        WebElement toRow = getRowByQueuePosition(toPos);
        Actions builder = new Actions(this);
        builder.clickAndHold(fromRow)
                .moveToElement(toRow)
                .moveByOffset(5, yOffset)
                .release()
                .build()
                .perform();
    }

    public WebElement getRowByQueuePosition(int pos){
        String xpath = String.format("//td[@class=\"nav customfield_10000\" and text()=%d]", pos);
        return waitForElement(xpath, 5);
    }

    public String getTicketIdByQueuePosition(int pos){
        String xpath = String.format("//tr[td/text()=%d]/td[@class=\"nav issuekey\"]", pos);
        return findElement(By.xpath(xpath)).getText();
    }

    public WebElement waitForElement(String xpath, int timeoutSeconds){
        manage().timeouts().implicitlyWait(timeoutSeconds, TimeUnit.SECONDS);
        return findElement(By.xpath(xpath));
    }

    public void removeIssuesAndField(){
        clearIssues();
        deleteQueueField();
    }

    public void clearIssues(){

    }

    public void createIssues(int count){
    }

    public void reindexIssues(){
    }

    public void addQueueField(){
        //...
        reindexIssues();
    }

    public void deleteQueueField(){

    }

    public boolean issuesEnumerationCorrect(){
        return true;
    }

    public void closeFewIssues() {

    }
}
