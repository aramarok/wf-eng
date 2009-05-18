package wf.cfg;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;

public class Configuratii {

    private static Configuratii appConfig;

    public static final String configFile = Constante.APP_PROPERTIES_FILE_NAME;

    private static Logger log = Logger.getLogger(Configuratii.class);

    public static String DB_CREATE_SCRIPT() {
	return getInstance().get("CREATEDB-SQL", "conf/db/create_db.sql");
    }

    public static final String getConnectionFactory() {
	return getInstance().get(Constante.WF_CONNECTIONFACTORY_FIELD,
		Constante.WF_CONNECTIONFACTORY_VALUE);
    }

    public static final String getDataSource() {
	return getInstance().get(Constante.WFDS_FIELD, Constante.WFDS_VALUE);
    }

    public static final String getEventsTopic() {
	return getInstance().get(Constante.WF_TOPIC_EVENTS_FIELD,
		Constante.WF_TOPIC_EVENTS_VALUE);
    }

    public static final String getInboxTopic() {
	return getInstance().get(Constante.WF_TOPIC_INBOX_FIELD,
		Constante.WF_TOPIC_INBOX_VALUE);
    }

    public synchronized static Configuratii getInstance() {
	if (appConfig == null) {
	    appConfig = new Configuratii();
	}
	return appConfig;
    }

    public static final String getOutboxQueue() {
	return getInstance().get(Constante.WF_QUEUE_OUTBOX_FIELD,
		Constante.WF_QUEUE_OUTBOX_VALUE);
    }

    public static final String getWfQueue() {
	return getInstance().get(Constante.WF_QUEUE_ENGINE_FIELD,
		Constante.WF_QUEUE_ENGINE_VALUE);
    }

    private final Properties config = new Properties();

    private Configuratii() {
	try {
	    URL url = this.getClass().getClassLoader().getResource(configFile);
	    if (url == null) {
		url = Thread.currentThread().getContextClassLoader()
			.getResource(configFile);
	    }
	    if (url != null) {
		InputStream inputStream = url.openStream();
		this.config.load(inputStream);
		inputStream.close();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public String get(final String key, final String def) {
	String v = (String) this.config.get(key);
	if (v == null) {
	    this.put(key, def);
	    return def;
	}
	return v;
    }

    public int getInt(final String key, final int i) {
	String v = (String) this.config.get(key);
	if (v == null) {
	    this.putInt(key, i);
	    return i;
	}
	return Integer.parseInt(v);
    }

    public void put(final String key, final String val) {
	this.config.put(key, val);
    }

    public void putInt(final String key, final int i) {
	this.config.put(key, String.valueOf(i));
    }

}
