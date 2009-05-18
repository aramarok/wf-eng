package wf.jms;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import org.apache.log4j.Logger;

public class MessagingExceptionListener implements ExceptionListener {

    private static Logger log = Logger
	    .getLogger(MessagingExceptionListener.class);

    public void onException(final JMSException e) {
	log.info("JMS Connection exception: " + e.getMessage());
    }

}
