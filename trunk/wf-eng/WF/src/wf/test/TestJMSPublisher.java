


package wf.test;

import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;

import wf.exceptions.WorkFlowException;
import wf.jms.JMSPublisher;
import wf.jms.JMSTopicConnection;
import wf.jms.MessageProperty;

public class TestJMSPublisher {

    public static void main (String[] args) throws WorkFlowException, JMSException  {

        JMSTopicConnection.initialize();

        List props = new ArrayList();
        MessageProperty mp = new MessageProperty();
        mp.name = "ProcName";
        mp.value = "ProcA";
        props.add (mp);

        String topicName = args[0];
        String msg = args[1];
        JMSPublisher.send (topicName, msg, props);
    }

}
