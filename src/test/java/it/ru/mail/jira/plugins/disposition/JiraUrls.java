package it.ru.mail.jira.plugins.disposition;

public class JiraUrls {
    public static String localHome = "http://localhost:2991/jira/secure/";
    public static String ciHome = "http://192.168.0.210:2991/jira/secure/";
    public static String dashboard = "Dashboard.jspa";
    public static String issues = "IssueNavigator.jspa";
    public static String customFields = "ViewCustomFields.jspa";
    public static String editIssueViewColumns = "ViewUserIssueColumns!default.jspa";
    public static String bulkEditIssues = "views/bulkedit/BulkEdit1!default.jspa?reset=true&tempMax=1";
    public static String reindexIssues = "ResetIssuesDispositionAction!default.jspa";
    public static String systemReindexing = "admin/jira/IndexAdmin.jspa";
    public static String groups = "admin/user/GroupBrowser.jspa";
    public static String editMembers = "admin/user/BulkEditUserGroups!default.jspa";
}
