package wf.jms;

import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import org.apache.log4j.Logger;
import wf.exceptions.WorkFlowException;

public class JMSSubscriber {

	private Topic topic = null;
	private TopicSession session = null;

	private static Logger log = Logger.getLogger(JMSSubscriber.class);

	public JMSSubscriber(MessageListener lner, String topicName, String selector)
			throws WorkFlowException {

		try {
			InitialContext iniCtx = JMSTopicConnection.getInitialContext();
			TopicConnection conn = JMSTopicConnection.getConnection();
			topic = (Topic) iniCtx.lookup(topicName);
			session = conn.createTopicSession(false,
					TopicSession.AUTO_ACKNOWLEDGE);
			TopicSubscriber recv;
			if (selector != null) {
				recv = session.createSubscriber(topic, selector, false);
			} else {
				recv = session.createSubscriber(topic);
			}
			recv.setMessageListener(lner);
			log.info("Created topic subscription for: " + topicName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WorkFlowException("Can't set up JMS Subscription");
		}
	}
}
