package wf.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import javax.jms.JMSException;
import wf.exceptions.ExceptieWF;
import wf.jms.ManagerEvenimente;

public class TestEventSubscriber {

    public static void main(final String[] args) throws ExceptieWF,
	    JMSException {

	String propFileName = args[0];
	Properties props = new Properties();
	try {
	    FileInputStream fi = new FileInputStream(propFileName);
	    props.load(fi);
	    fi.close();
	} catch (FileNotFoundException fx) {
	    System.out.print("Property file not found: " + fx.getMessage());
	    return;
	} catch (IOException e) {
	    System.out.print("Failed to read property file: " + e.getMessage());
	    return;
	}

	new TestEventSubscriber().start(props);
    }

    public void start(final Properties props) throws ExceptieWF {
	@SuppressWarnings("unused")
	ManagerEvenimente subs = new ManagerEvenimente(props);
    }

}
