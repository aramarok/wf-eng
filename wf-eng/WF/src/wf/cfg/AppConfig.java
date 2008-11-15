package wf.cfg;

import org.apache.log4j.Logger;

import java.util.Properties;
import java.net.URL;
import java.io.InputStream;

public class AppConfig {

	public static final String configFile = "app.properties";

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
		return getInstance().get("WF_CONNECTIONFACTORY",
				"ConnectionFactory");
	}

	public static final String getInboxTopic() {
		return getInstance().get("WFTOPIC_INBOX",
				"topic/WFTOPIC_INBOX");
	}

	public static final String getOutboxQueue() {
		return getInstance().get("WFQUEUE_OUTBOX",
				"queue/WFQUEUE_OUTBOX");
	}

	public static final String getEventsTopic() {
		return getInstance().get("WFTOPIC_EVENTS",
				"topic/WFTOPIC_EVENTS");
	}

	public static final String getWfQueue() {
		return getInstance().get("WFQUEUE_ENGINE",
				"queue/WFQUEUE_ENGINE");
	}

	public static final String getDataSource() {
		return getInstance().get("WFDS", "java:/WFDS");
	}

	public static String DB_CREATE_SCRIPT() {
		return getInstance().get("CREATEDB-SQL",
				"conf/create_db.sql");
	}

}
