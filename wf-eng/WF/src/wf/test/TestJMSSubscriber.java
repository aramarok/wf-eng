


package wf.test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import wf.cfg.XflowConfig;
import wf.exceptions.XflowException;
import wf.jms.JMSSubscriber;
import wf.jms.JMSTopicConnection;

public class TestJMSSubscriber implements MessageListener {

    public void onMessage (Message msg) {

       TextMessage tm = (TextMessage) msg;
       try {
           System.out.println ("Got a message: " + tm.getText());
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    public void start() throws XflowException {
        JMSSubscriber subs = new JMSSubscriber (this, XflowConfig.XFLOW_TOPIC(), "ProcName in ('ProcA')");
    }

    public static void main (String[] args) throws XflowException, JMSException {
        JMSTopicConnection.initialize();
        new TestJMSSubscriber().start();
    }

}
