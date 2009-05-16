package wf.test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import wf.cfg.AppConfig;
import wf.exceptions.ExceptieWF;
import wf.jms.InregistrareJMS;
import wf.jms.ConexiuneTopicJMS;

public class TestJMSSubscriber implements MessageListener {

    public static void main(final String[] args) throws ExceptieWF,
	    JMSException {
	ConexiuneTopicJMS.initialize();
	new TestJMSSubscriber().start();
    }

    public void onMessage(final Message msg) {

	TextMessage tm = (TextMessage) msg;
	try {
	    System.out.println("Primit mesaj: " + tm.getText());
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void start() throws ExceptieWF {
	@SuppressWarnings("unused")
	InregistrareJMS subs = new InregistrareJMS(this, AppConfig.getInboxTopic(),
		"ProcName din ('ProcA')");
    }

}
