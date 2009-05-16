package wf.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import org.apache.log4j.Logger;

public class ListenerExcepti implements ExceptionListener {

    private static Logger log = Logger.getLogger(ListenerExcepti.class);

    public void onException(final JMSException e) {
	log.info("JMS Connection exception: " + e.getMessage());
    }

}
