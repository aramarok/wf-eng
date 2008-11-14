
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

import wf.exceptions.WorkFlowException;

public class JMSPublisher {

  private static Logger log = Logger.getLogger(JMSPublisher.class);

  private static boolean sendingEnabled = true;

  public static boolean isSendingEnabled() {
    return sendingEnabled;
  }

  public static void setSendingEnabled(boolean enabled) {
    sendingEnabled = enabled;
  }

  public static void send (String topicName, String smsg, List props) throws WorkFlowException {
     send ( topicName, smsg, null, props);
  }

  public static void send (String topicName, byte[] byteMsg, List props) throws WorkFlowException {
    send ( topicName, null, byteMsg,  props);
  }

  private static void send (String topicName, String txtNsg, byte[] byteMsg, List props) throws WorkFlowException {
    if( ! sendingEnabled ) return;
    try {
      InitialContext iniCtx = JMSTopicConnection.getInitialContext();
      TopicConnection conn = JMSTopicConnection.getConnection();
      Topic topic = (Topic) iniCtx.lookup(topicName);
      TopicSession session = conn.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
      TopicPublisher pub = session.createPublisher(topic);
      Message message = null;
      if( byteMsg != null ){
        BytesMessage msg = session.createBytesMessage();
        msg.writeBytes ( byteMsg);
        message = msg;
      }else if( txtNsg != null ){
        TextMessage msg = session.createTextMessage(txtNsg);
        message = msg;
      }
      if (props != null) {
        for (int i = 0; i < props.size(); i++) {
          MessageProperty mp = (MessageProperty)props.get(i);
          log.info ("Setting message property: " + mp.name + " " + mp.value);
          message.setStringProperty (mp.name, mp.value);
        }
      }
      pub.publish (message);
      pub.close();
      log.info ("Published message for topic: " + topicName);
    } catch (Exception e) {
      throw new WorkFlowException ("Can't publish message on JMS topic: " + topicName, e);
    }
  }
}

