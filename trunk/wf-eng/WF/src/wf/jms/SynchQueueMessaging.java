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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;

import wf.cfg.XflowConfig;
import wf.exceptions.XflowException;
import wf.jms.model.Request;
import wf.jms.model.Response;


public class SynchQueueMessaging {

  private static QueueConnection qconn = null;
  private static QueueSession qsession = null;
  private static Queue receiveQueue = null;
  private static Queue wfQueue = null;

  static {

    try {
      /*URL jndiProps = SynchQueueMessaging.class.getClassLoader().getResource( "jndi.properties");
      // Tomcat doesn't seem to find jndi.properties -- so we might have to load manually
      boolean jndiFileFound = (jndiProps != null);
      InitialContext iniCtx = null;
      File f = new File("jndi.properties");

      try {
          FileReader fr = new FileReader(f);
      } catch (FileNotFoundException e) {
          jndiFileFound = false;
}
      if (jndiFileFound) {
          System.out.println ("Loading from jndi.properties file");
          Properties props = new Properties ( );

          props.load ( new FileInputStream("jndi.properties"));
          String namingFactory = (String)props.get("java.naming.factory.initial");
          String providerUrl = (String)props.get("java.naming.provider.url");
          String factoryUrlPkg = (String)props.get("java.naming.factory.url.pkgs");
          Hashtable env = new Hashtable();
          env.put ("java.naming.factory.initial", namingFactory);
          env.put ("java.naming.provider.url", providerUrl);
          env.put ("java.naming.factory.url.pkgs", factoryUrlPkg);
          iniCtx = new InitialContext( props );
      } else {
          iniCtx = new InitialContext();
      }*/
      InitialContext iniCtx = new InitialContext();

      Object tmp = iniCtx.lookup(XflowConfig.XFLOW_CONNECTION_FACTORY());
      QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
      qconn = qcf.createQueueConnection();

      qsession = qconn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
      receiveQueue = (Queue) iniCtx.lookup(XflowConfig.XFLOW_QUEUE());
      //receiveQueue =  qsession.createQueue("testQueue");

      qconn.start();

      wfQueue = (Queue) iniCtx.lookup(XflowConfig.WORKFLOWENGINE_QUEUE());
      JMSShutdownHook shook = new JMSShutdownHook();
      Runtime.getRuntime().addShutdownHook(shook);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void close() throws JMSException {
    qconn.close();
  }

  public static Response sendRequest(Request req) throws JMSException, IOException,
      ClassNotFoundException,
      XflowException {

    // Send the request
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream s = new ObjectOutputStream(out);

    s.writeObject(req);
    s.flush();
    byte[] barr = out.toByteArray();
    String replyName = req.replyName;
    QueueReceiver receiver = qsession.createReceiver(receiveQueue,
        "ReplyName in ('" + replyName + "')");
    QueueSender sender = qsession.createSender(wfQueue);

    BytesMessage m = qsession.createBytesMessage();
    m.writeBytes(barr);
    m.setStringProperty("ReplyName", replyName);

    m.setJMSReplyTo(receiveQueue);
    sender.send(m);

    // Now get the response
    Message msg = receiver.receive(5000);
    Response response = null;
    if (msg != null) {
      BytesMessage bytesMessage = (BytesMessage) msg;
      barr = new byte[10000];
      bytesMessage.readBytes(barr);
      ByteArrayInputStream in = new ByteArrayInputStream(barr);
      ObjectInputStream sin = new ObjectInputStream(in);
      response = (Response) sin.readObject();
    } else {
      throw new XflowException("Response not received from server within 5 seconds.");
    }
    return response;
  }

}

