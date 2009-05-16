package wf.cfg;

public interface Constante {

    public static final String CLIENT_ICON_LOCATION = "/wf/client/manager/icon/";
    public static final String APP_PROPERTIES_FILE_NAME = "app.properties";
    public static final String SQL_MAP_LOCATION = "wf/server/controller/sqlmap.xml";

    public static final String DB_USERNAME_FIELD = "DB_USERNAME";
    public static final String DB_PASSWORD_FIELD = "DB_PASSWORD";
    public static final String DB_DRIVER_FIELD = "DB_DRIVER";
    public static final String DB_URL_FIELD = "DB_URL";

    public static final String WFDS_FIELD = "WFDS";
    public static final String WFDS_VALUE = "java\\:/WFDS";
    public static final String WF_CONNECTIONFACTORY_FIELD = "WF_CONNECTIONFACTORY";
    public static final String WF_CONNECTIONFACTORY_VALUE = "ConnectionFactory";
    public static final String WF_TOPIC_INBOX_FIELD = "WFTOPIC_INBOX";
    public static final String WF_TOPIC_INBOX_VALUE = "topic/WFTOPIC_INBOX";
    public static final String WF_TOPIC_EVENTS_FIELD = "WFTOPIC_EVENTS";
    public static final String WF_TOPIC_EVENTS_VALUE = "topic/WFTOPIC_EVENTS";
    public static final String WF_QUEUE_OUTBOX_FIELD = "WFQUEUE_OUTBOX";
    public static final String WF_QUEUE_OUTBOX_VALUE = "queue/WFQUEUE_OUTBOX";
    public static final String WF_QUEUE_ENGINE_FIELD = "WFQUEUE_ENGINE";
    public static final String WF_QUEUE_ENGINE_VALUE = "queue/WFQUEUE_ENGINE";
}