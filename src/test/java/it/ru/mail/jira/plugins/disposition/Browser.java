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
    public static final String CI_URL="http://192.168.0.210:2990/jira/secure/Dashboard.jspa";
    public static final String LOCAL_URL="http://localhost:2990/jira/secure/Dashboard.jspa";

    private static final int QUEUE_START = 1;
    private static final int QUEUE_END = 10;
    private List<String> queue = new ArrayList<String>();
    private String homeUrl;

    public Browser(){
        super();
        homeUrl = LOCAL_URL;
        get(homeUrl);
    }

    public Browser(String url){
        super();
        homeUrl = url;
        get(homeUrl);
    }

    public void loginWithUsernameAndPassword(String username, String password){
        WebElement frame = findElement(By.xpath(Elements.loginFrame));
        switchTo().frame(frame);
        WebElement login_field = findElement(By.xpath(Elements.loginField));
        WebElement password_field = findElement(By.xpath(Elements.passwordField));
        WebElement login_button = findElement(By.xpath(Elements.loginButton));
        login_field.sendKeys(username);
        password_field.sendKeys(password);
        login_button.click();
    }

    public void logOut(){
        WebElement userMenu = findElement(By.xpath(Elements.userMenu));
        userMenu.click();
        WebElement logOutButton = findElement(By.xpath(Elements.logoutButton));
        logOutButton.click();
        navigate().to(homeUrl);
    }

    public void openIssuesList(){
        WebElement issuesLink = waitForElement(Elements.issuesLink, 5);
        issuesLink.click();
        WebElement searchButton = findElement(By.xpath(Elements.searchButton));
        searchButton.click();
        WebElement sortByQueueButton = findElement(By.xpath(Elements.sortByQueueButton));
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
        String xpath = String.format(Elements.queueFieldOnPos, pos);
        return waitForElement(xpath, 5);
    }

    public String getTicketIdByQueuePosition(int pos){
        String xpath = String.format(Elements.rowOnPos, pos);
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
