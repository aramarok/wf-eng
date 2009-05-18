package wf.test;

import java.util.ArrayList;
import java.util.List;
import javax.jms.JMSException;
import wf.exceptions.ExceptieWF;
import wf.jms.JMSPublisher;
import wf.jms.JMSTopicConnection;
import wf.jms.MessageProperty;

public class TestJMSPublisher {

    @SuppressWarnings("unchecked")
    public static void main(final String[] args) throws ExceptieWF,
	    JMSException {

	JMSTopicConnection.initialize();

	List props = new ArrayList();
	MessageProperty mp = new MessageProperty();
	mp.name = "ProcName";
	mp.value = "ProcA";
	props.add(mp);

	String topicName = args[0];
	String msg = args[1];
	JMSPublisher.send(topicName, msg, props);
    }

}
