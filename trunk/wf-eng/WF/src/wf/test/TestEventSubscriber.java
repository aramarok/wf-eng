


package wf.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.jms.JMSException;

import wf.exceptions.XflowException;
import wf.jms.EventsHandler;

public class TestEventSubscriber {

    public void start(Properties props) throws XflowException {
        EventsHandler subs = new EventsHandler (props);
    }

    public static void main (String[] args) throws XflowException, JMSException {

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

}
