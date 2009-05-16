package wf.jms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Properties;
import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.naming.InitialContext;
import wf.cfg.AppConfig;

@SuppressWarnings("unchecked")
public class ConexiuneTopicJMS {

    private static TopicConnection conexiune = null;
    private static InitialContext context;
    private static boolean merge = false;

    static {

	try {
	    boolean jndiOK = true;
	    File fisier = new File("jndi.properties");
	    try {
		@SuppressWarnings("unused")
		FileReader cititor = new FileReader(fisier);
	    } catch (FileNotFoundException e) {
		jndiOK = false;
	    }

	    if (jndiOK) {
		Properties proprietati = new Properties();
		proprietati.load(new FileInputStream("jndi.properties"));
		String fNume = (String) proprietati
			.get("java.naming.factory.initial");
		String url = (String) proprietati
			.get("java.naming.provider.url");
		String pack = (String) proprietati
			.get("java.naming.factory.url.pkgs");
		Hashtable ht = new Hashtable();
		ht.put("java.naming.factory.initial", fNume);
		ht.put("java.naming.provider.url", url);
		ht.put("java.naming.factory.url.pkgs", pack);
		context = new InitialContext(ht);
	    } else {
		context = new InitialContext();
	    }

	    Object obiect = context.lookup(AppConfig.getConnectionFactory());
	    TopicConnectionFactory factory = (TopicConnectionFactory) obiect;
	    conexiune = factory.createTopicConnection();
	} catch (Exception e) {
	}
    }

    public static void close() throws JMSException {
	conexiune.close();
    }

    public static TopicConnection getConnection() {
	return conexiune;
    }

    public static InitialContext getInitialContext() {
	return context;
    }

    public static void initialize() throws JMSException {

	if (merge) {
	    return;
	}
	merge = true;
	OprireJMS stop = new OprireJMS();
	Runtime.getRuntime().addShutdownHook(stop);
	start();
    }

    public static void start() throws JMSException {
	conexiune.start();
    }

    public static void stop() throws JMSException {
	conexiune.stop();
    }
}
