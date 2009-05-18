package wf.jms;

import java.util.List;
import javax.jms.BytesMessage;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.InitialContext;
import org.apache.log4j.Logger;
import wf.exceptions.ExceptieWF;

public class JMSPublisher {

    private static Logger log = Logger.getLogger(JMSPublisher.class);

    private static boolean trimite = true;

    public static boolean isSendingEnabled() {
	return trimite;
    }

    @SuppressWarnings("unchecked")
    public static void send(final String topicName, final byte[] byteMsg,
	    final List props) throws ExceptieWF {
	send(topicName, null, byteMsg, props);
    }

    @SuppressWarnings("unchecked")
    private static void send(final String topicName, final String txtNsg,
	    final byte[] byteMsg, final List props) throws ExceptieWF {
	if (!trimite) {
	    return;
	}
	try {
	    InitialContext iniCtx = JMSTopicConnection.getInitialContext();
	    TopicConnection conn = JMSTopicConnection.getConnection();
	    Topic topic = (Topic) iniCtx.lookup(topicName);
	    TopicSession session = conn.createTopicSession(false,
		    TopicSession.AUTO_ACKNOWLEDGE);
	    TopicPublisher pub = session.createPublisher(topic);
	    Message message = null;
	    if (byteMsg != null) {
		BytesMessage msg = session.createBytesMessage();
		msg.writeBytes(byteMsg);
		message = msg;
	    } else if (txtNsg != null) {
		TextMessage msg = session.createTextMessage(txtNsg);
		message = msg;
	    }
	    if (props != null) {
		for (int i = 0; i < props.size(); i++) {
		    MessageProperty mp = (MessageProperty) props.get(i);
		    log.info("Setting mesaj property: " + mp.name + " "
			    + mp.value);
		    message.setStringProperty(mp.name, mp.value);
		}
	    }
	    pub.publish(message);
	    pub.close();
	    log.info("Published mesaj for topic: " + topicName);
	} catch (Exception e) {
	    throw new ExceptieWF("Can't publish mesaj on JMS topic: "
		    + topicName, e);
	}
    }

    @SuppressWarnings("unchecked")
    public static void send(final String topicName, final String smsg,
	    final List props) throws ExceptieWF {
	send(topicName, smsg, null, props);
    }

    public static void setSendingEnabled(final boolean enabled) {
	trimite = enabled;
    }
}
