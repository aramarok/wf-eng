package wf.test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import wf.exceptions.WorkFlowException;
import wf.jms.JMSSubscriber;
import wf.jms.JMSTopicConnection;

public class TestJMSSubscriber2 implements MessageListener {

	public void onMessage(Message msg) {

		TextMessage tm = (TextMessage) msg;
		try {
			System.out.println("Got a message: " + tm.getText());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start(String topic) throws WorkFlowException {
		JMSSubscriber subs = new JMSSubscriber(this, topic, null);
	}

	public static void main(String[] args) throws WorkFlowException,
			JMSException {
		JMSTopicConnection.initialize();
		String topic = args[0];
		new TestJMSSubscriber2().start(topic);
	}

}
