


package wf.test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import wf.cfg.AppConfig;
import wf.exceptions.WorkFlowException;
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

    public void start() throws WorkFlowException {
        JMSSubscriber subs = new JMSSubscriber (this, AppConfig.XFLOW_TOPIC(), "ProcName in ('ProcA')");
    }

    public static void main (String[] args) throws WorkFlowException, JMSException {
        JMSTopicConnection.initialize();
        new TestJMSSubscriber().start();
    }

}
