package wf.cfg;

import org.apache.log4j.Logger;

import wf.client.WFConstants;

import java.util.Properties;
import java.net.URL;
import java.io.InputStream;

public class AppConfig {

	public static final String configFile = WFConstants.APP_PROPERTIES_FILE_NAME;

	private static Logger log = Logger.getLogger(AppConfig.class);

	private static AppConfig appConfig;

	private Properties config = new Properties();

	private AppConfig() {
		try {
			URL url = this.getClass().getClassLoader().getResource(configFile);
			if (url == null) {
				url = Thread.currentThread().getContextClassLoader()
						.getResource(configFile);
			}
			if (url != null) {
				InputStream inputStream = url.openStream();
				config.load(inputStream);
				inputStream.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public synchronized static AppConfig getInstance() {
		if (appConfig == null) {
			appConfig = new AppConfig();
		}
		return appConfig;
	}

	public String get(String key, String def) {
		String v = (String) config.get(key);
		if (v == null) {
			log.info("key " + key + " was not found in " + configFile
					+ ". Will use default value [" + def + "]");
			put(key, def);
			return def;
		}
		return v;
	}

	public int getInt(String key, int i) {
		String v = (String) config.get(key);
		if (v == null) {
			log.info("key " + key + " was not found in " + configFile
					+ ". Will use default value [" + i + "]");
			putInt(key, i);
			return i;
		}
		return Integer.parseInt(v);
	}

	public void put(String key, String val) {
		config.put(key, val);
	}

	public void putInt(String key, int i) {
		config.put(key, String.valueOf(i));
	}

	public static final String getConnectionFactory() {
		return getInstance().get(WFConstants.WF_CONNECTIONFACTORY_FIELD,
				WFConstants.WF_CONNECTIONFACTORY_VALUE);
	}

	public static final String getInboxTopic() {
		return getInstance().get(WFConstants.WF_TOPIC_INBOX_FIELD,
				WFConstants.WF_TOPIC_INBOX_VALUE);
	}

	public static final String getOutboxQueue() {
		return getInstance().get(WFConstants.WF_QUEUE_OUTBOX_FIELD,
				WFConstants.WF_QUEUE_OUTBOX_VALUE);
	}

	public static final String getEventsTopic() {
		return getInstance().get(WFConstants.WF_TOPIC_EVENTS_FIELD,
				WFConstants.WF_TOPIC_EVENTS_VALUE);
	}

	public static final String getWfQueue() {
		return getInstance().get(WFConstants.WF_QUEUE_ENGINE_FIELD,
				WFConstants.WF_QUEUE_ENGINE_VALUE);
	}

	public static final String getDataSource() {
		return getInstance().get(WFConstants.WFDS_FIELD, WFConstants.WFDS_VALUE);
	}

	public static String DB_CREATE_SCRIPT() {
		return getInstance().get("CREATEDB-SQL", "conf/db/create_db.sql");
	}

}
