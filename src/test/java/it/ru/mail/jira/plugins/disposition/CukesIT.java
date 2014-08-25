package it.ru.mail.jira.plugins.disposition;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(format="pretty", features="features")
public class CukesIT {
    public static final Browser browser = new Browser();

    @BeforeClass()
    public static void setUp(){
        browser.loginWithUsernameAndPassword("admin", "admin");
        browser.createProject("Test");
        browser.preconfigure();
    }

    @AfterClass()
    public static void tearDown(){
        browser.quit();
    }
}
