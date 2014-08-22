package it.ru.mail.jira.plugins.disposition;

import cucumber.api.java.ru.*;
import static org.junit.Assert.assertTrue;

public class StepDef {
    private static final Browser browser = new Browser();

    @Дано("^В проекте (\\d+) тикетов$")
    public void В_проекте_н_тикетов(int n){
        browser.createIssues(n);
    }

    @Если("^Добавить очередь$")
    public void Добавить_очередь(){
        Создать_поле_очереди();
        Настроить_отображение_поля_очереди();
        Реиндексировать_тикеты();
    }

    @И("^Открыть список тикетов$")
    public void Открыть_список_тикетов(){
        browser.openIssuesList();
        browser.sortIssues();
    }

    @И("^Создать поле очереди$")
    public void Создать_поле_очереди(){
        browser.createQueueField();
    }

    @И("^Настроить отображение поля очереди$")
    public void Настроить_отображение_поля_очереди(){
        browser.configureIssuesTable();
    }

    @И("^Реиндексировать тикеты$")
    public void Реиндексировать_тикеты(){
        browser.reindexIssues();
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

    @Дано("^В очереди (\\d+) тикетов$")
    public void В_очереди_н_тикетов(int n){
        В_проекте_н_тикетов(n);
        Реиндексировать_тикеты();
    }

    @И("^Переместить тикет с (\\d+) места на (\\d+) место$")
    public void Переместить_тикет_с_места_на_место(int fromPos, int toPos){
        browser.moveIssue(fromPos, toPos);
    }

    @То("^Нумерация тикетов в очереди изменяется$")
    public void Нумерация_тикетов_в_очереди_изменяется(){
        assertTrue(browser.queueCorrect());
    }

    @Допустим("^Я вхожу в систему с логином \"([^\"]*)\" и паролем \"([^\"]*)\"$")
    public void Я_вхожу_в_систему_с_логином_и_паролем(String login, String password){
        browser.loginWithUsernameAndPassword(login, password);
    }

    @Допустим("^Я администратор$")
    public void Я_администратор(){
        Я_вхожу_в_систему_с_логином_и_паролем("admin", "admin");
    }

    @И("^Я на главной странице$")
    public void Я_на_главной_странице(){
        browser.goHome();
    }

    @И("^Создан проект \"([^\"]*)\"$")
    public void Создан_проект(String projectName){
        browser.createProject(projectName);
    }

    @И("^Я вхожу в управляющие группы$")
    public void Я_вхожу_в_управляющие_группы(){
        browser.configureGroups();
    }

    @И("^Я удаляю тикеты$")
    public void Я_удаляю_тикеты(){
        browser.removeIssues();
    }

    @И("^Я завершаю тесирование$")
    public void Я_завершаю_тесирование(){
        browser.quit();
    }
}
