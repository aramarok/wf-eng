/*
* ====================================================================
*
* XFLOW - Process Management System
* Copyright (C) 2003 Rob Tan
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions, and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions, and the disclaimer that follows
*    these conditions in the documentation and/or other materials
*    provided with the distribution.
*
* 3. The name "XFlow" must not be used to endorse or promote products
*    derived from this software without prior written permission.  For
*    written permission, please contact rcktan@yahoo.com
*
* 4. Products derived from this software may not be called "XFlow", nor
*    may "XFlow" appear in their name, without prior written permission
*    from the XFlow Project Management (rcktan@yahoo.com)
*
* In addition, we request (but do not require) that you include in the
* end-user documentation provided with the redistribution and/or in the
* software itself an acknowledgement equivalent to the following:
*     "This product includes software developed by the
*      XFlow Project (http://xflow.sourceforge.net/)."
* Alternatively, the acknowledgment may be graphical using the logos
* available at http://xflow.sourceforge.net/
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE XFLOW AUTHORS OR THE PROJECT
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
*
* ====================================================================
* This software consists of voluntary contributions made by many
* individuals on behalf of the XFlow Project and was originally
* created by Rob Tan (rcktan@yahoo.com)
* For more information on the XFlow Project, please see:
*           <http://xflow.sourceforge.net/>.
* ====================================================================
*/
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

import wf.exceptions.XflowException;

public class JMSPublisher {

  private static Logger log = Logger.getLogger(JMSPublisher.class);

  private static boolean sendingEnabled = true;

  public static boolean isSendingEnabled() {
    return sendingEnabled;
  }

  public static void setSendingEnabled(boolean enabled) {
    sendingEnabled = enabled;
  }

  public static void send (String topicName, String smsg, List props) throws XflowException {
     send ( topicName, smsg, null, props);
  }

  public static void send (String topicName, byte[] byteMsg, List props) throws XflowException {
    send ( topicName, null, byteMsg,  props);
  }

  private static void send (String topicName, String txtNsg, byte[] byteMsg, List props) throws XflowException {
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
      throw new XflowException ("Can't publish message on JMS topic: " + topicName, e);
    }
  }
}

