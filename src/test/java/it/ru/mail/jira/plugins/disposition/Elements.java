package it.ru.mail.jira.plugins.disposition;

public class Elements {
    public static String loginFrame = "//iframe[@id=\"gadget-0\"]";
    public static String loginField = "//input[@id=\"login-form-username\"]";
    public static String passwordField = "//input[@id=\"login-form-password\"]";
    public static String loginButton = "//input[@id=\"login\"]";
    public static String userMenu = "//nav[@class=\"global\"]//a/span";
    public static String logoutButton = "//a[@id=\"log_out\"]";
    public static String issuesLink = "//a[@id=\"find_link\"]";
    public static String searchButton = "//input[@id=\"issue-filter-submit\"]";
    public static String sortByQueueButton = "//span[@title=\"Sort By Queue Position\"]";
    public static String queueFieldOnPos = "//td[@class=\"nav customfield_10000\" and text()=%d]";
    public static String rowOnPos = "//tr[td/text()=%d]/td[@class=\"nav issuekey\"]";
    public static String toolsButton = "//span[text()=\"Tools\"]";
    public static String simpleSearchLink = "";
}
