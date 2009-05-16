package wf.test;

import java.util.ArrayList;
import java.util.List;
import javax.jms.JMSException;
import wf.exceptions.ExceptieWF;
import wf.jms.PublisherJMS;
import wf.jms.ConexiuneTopicJMS;
import wf.jms.ProprietatiMesaje;

public class TestJMSPublisher {

    @SuppressWarnings("unchecked")
    public static void main(final String[] args) throws ExceptieWF,
	    JMSException {

	ConexiuneTopicJMS.initialize();

	List props = new ArrayList();
	ProprietatiMesaje mp = new ProprietatiMesaje();
	mp.name = "ProcName";
	mp.value = "ProcA";
	props.add(mp);

	String topicName = args[0];
	String msg = args[1];
	PublisherJMS.send(topicName, msg, props);
    }

}
