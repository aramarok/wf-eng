package wf.test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import wf.exceptions.ExceptieWF;
import wf.jms.JMSSubscriber;
import wf.jms.JMSTopicConnection;

public class TestJMSSubscriber2 implements MessageListener {

    public static void main(final String[] args) throws ExceptieWF,
	    JMSException {
	JMSTopicConnection.initialize();
	String topic = args[0];
	new TestJMSSubscriber2().start(topic);
    }

    public void onMessage(final Message msg) {

	TextMessage tm = (TextMessage) msg;
	try {
	    System.out.println("Primit mesaj: " + tm.getText());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void start(final String topic) throws ExceptieWF {
	@SuppressWarnings("unused")
	JMSSubscriber subs = new JMSSubscriber(this, topic, null);
    }

}
