package wf.jms;

import javax.jms.MessageListener;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;
import wf.exceptions.ExceptieWF;

public class InregistrareJMS {

    private TopicSession sesiune = null;

    private Topic topic = null;

    public InregistrareJMS(final MessageListener ml, final String numeTopic,
	    final String s) throws ExceptieWF {

	try {
	    InitialContext context = ConexiuneTopicJMS.getInitialContext();
	    TopicConnection conexiune = ConexiuneTopicJMS.getConnection();
	    this.topic = (Topic) context.lookup(numeTopic);
	    this.sesiune = conexiune.createTopicSession(false,
		    TopicSession.AUTO_ACKNOWLEDGE);
	    TopicSubscriber subs;
	    if (s != null) {
		subs = this.sesiune.createSubscriber(this.topic, s, false);
	    } else {
		subs = this.sesiune.createSubscriber(this.topic);
	    }
	    subs.setMessageListener(ml);
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new ExceptieWF("nu merge JMS !");
	}
    }
}
