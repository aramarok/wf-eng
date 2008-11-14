
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

import wf.cfg.AppConfig;
import wf.exceptions.WorkFlowException;
import wf.jms.model.Request;
import wf.jms.model.Response;


public class SynchQueueMessaging {

  private static QueueConnection qconn = null;
  private static QueueSession qsession = null;
  private static Queue receiveQueue = null;
  private static Queue wfQueue = null;

  static {

    try {
      
      InitialContext iniCtx = new InitialContext();

      Object tmp = iniCtx.lookup(AppConfig.XFLOW_CONNECTION_FACTORY());
      QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
      qconn = qcf.createQueueConnection();

      qsession = qconn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
      receiveQueue = (Queue) iniCtx.lookup(AppConfig.XFLOW_QUEUE());

      qconn.start();

      wfQueue = (Queue) iniCtx.lookup(AppConfig.WORKFLOWENGINE_QUEUE());
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
      WorkFlowException {
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
    
    System.out.println("Receiver = " + receiver);
    System.out.println("Sender = " + sender);
    
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
      throw new WorkFlowException("Response not received from server within 5 seconds.");
    }
    return response;
  }

}

