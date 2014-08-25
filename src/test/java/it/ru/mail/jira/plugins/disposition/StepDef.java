package it.ru.mail.jira.plugins.disposition;

import cucumber.api.java.After;
import cucumber.api.java.ru.*;
import static org.junit.Assert.assertTrue;
import static it.ru.mail.jira.plugins.disposition.CukesIT.browser;

public class StepDef {

    @After()
    public void afterScenario(){
        browser.removeIssues();
    }

    @Дано("^В проекте (\\d+) тикетов$")
    public void В_проекте_н_тикетов(int n){
        browser.createIssues(n);
    }

    @И("^Открыть список тикетов$")
    public void Открыть_список_тикетов(){
        browser.openIssuesList();
        browser.sortIssues();
    }

    @То("^Тикеты пронумерованы$")
    public void Тикеты_пронумерованы(){
        assertTrue(browser.issuesEnumerated());
    }

    @И("^Один из тикетов закрыт$")
    public void Один_из_тикетов_закрыт(){
        browser.issueIsClosed();
    }

    @То("^Закрытые тикеты не входят в очередь$")
    public void Закрытые_тикеты_не_входят_в_очередь(){
        assertTrue(browser.noClosedTicketsInQueue());
    }

    @И("^Переместить тикет с (\\d+) места на (\\d+) место$")
    public void Переместить_тикет_с_места_на_место(int fromPos, int toPos){
        browser.moveIssue(fromPos, toPos);
    }

    @То("^Нумерация тикетов в очереди изменяется$")
    public void Нумерация_тикетов_в_очереди_изменяется(){
        assertTrue(browser.queueCorrect());
    }

    @Допустим("^Тикеты реиндексированы$")
    public void Тикеты_реиндексированы(){
        browser.reindexIssues();
    }
}
