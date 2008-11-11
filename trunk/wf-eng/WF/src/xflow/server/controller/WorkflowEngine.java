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

package xflow.server.controller;

import org.apache.log4j.Logger;
import xflow.common.XflowConfig;
import xflow.protocol.Request;
import xflow.protocol.Response;
import xflow.security.Authenticator;
import xflow.security.XflowUserAuthenticator;
import xflow.util.Persistence;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.*;

public class WorkflowEngine implements MessageDrivenBean, MessageListener {

  public static final String FLOW_TYPE_XFLOW = "XFLOW";

  private static Logger log = Logger.getLogger(WorkflowEngine.class);

  private RequestHandlerFactory requestHandlerFactory = new RequestHandlerFactory();

  private Authenticator authenticator = null;

  private MessageDrivenContext ctx = null;
  private QueueConnection conn;
  private QueueSession session;



  public WorkflowEngine() {
    log.info ("WorkflowEngine.constructor, this=" + hashCode());
    //log.debug ("constructor.StackTrace", new Throwable("constructor"));
    Persistence.init();
  }

  public void setMessageDrivenContext(MessageDrivenContext ctx)
  {
    this.ctx = ctx;
    log.info("WorkflowEngine.setMessageDrivenContext, this="+hashCode());
  }

  public void ejbCreate()
  {
    log.info("WorkflowEngine.ejbCreate, this="+hashCode());
    try {
      setupPTP();
    }
    catch(Exception e) {
      log.error ("Failed to init WorkflowEngine", e);
      throw new EJBException("Failed to init WorkflowEngine", e);
    }
  }

  public void ejbRemove() {

    log.info("WorkflowEngine.ejbRemove, this="+hashCode());
    ctx = null;
    try {
      if (session != null) {
        session.close();
      }
      if (conn != null) {
        conn.close();
      }
    } catch(JMSException e) {
      log.error("ejbRemove error", e);
    }
  }

  public void onMessage(Message msg) {

    log.info("WorkflowEngine.onMessage, this=" + hashCode());
    try {
      Queue dest = (Queue) msg.getJMSReplyTo();
      log.info ("Reply queue is: " + dest.getQueueName());
      String procName = msg.getStringProperty("ReplyName");
      log.info ("procName = " + procName);

      BytesMessage bytesMessage = (BytesMessage)msg;
      byte[] barr = new byte[10000];
      bytesMessage.readBytes (barr);

      ByteArrayInputStream in = new ByteArrayInputStream(barr);
      ObjectInputStream sin = new ObjectInputStream(in);
      Request request = (Request) sin.readObject();

      // Authenticate the request
      String userName = request.user.getName();
      String password = request.user.getPassword();
      log.info ("userName = " + userName + " password = " + password);
      if (authenticator.authenticate(userName, password) == false) {
        // Authentication failed
        Response authFailedResponse = new Response();
        authFailedResponse.responseCode = Response.FAILURE;
        authFailedResponse.message = "Authentication failed for " + userName;
        sendReply(procName, dest, authFailedResponse);
      } else {
        // Authentication succeeded -- proceed in servicing the request
        Response response = handle( request );
        sendReply(procName, dest, response);
      }
    } catch(Throwable t) {
      log.error("onMessage error", t);
    }

  }

  public Response handle(Request req) {
    RequestHandler rH = requestHandlerFactory.getHandlerFor( req );
    return rH.handle( req );

  }

  private void setupPTP() throws JMSException, NamingException, ClassNotFoundException,
      InstantiationException, IllegalAccessException  {

    InitialContext iniCtx = new InitialContext();
    Object tmp = iniCtx.lookup(XflowConfig.XFLOW_CONNECTION_FACTORY());
    //Object tmp = iniCtx.lookup("java:comp/env/jms/QCF");
    QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
    conn = qcf.createQueueConnection();
    session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
    conn.start();

    String authClassName = (String)iniCtx.lookup("java:comp/env/authenticator");
    if( authClassName == null || authClassName.length() == 0) {
      log.info( "Authenticator was not supplied, use default. ");
      authClassName = XflowUserAuthenticator.class.getName();
    }
    log.info ("Authenticator is: " + authClassName);
    authenticator = (Authenticator)Class.forName(authClassName).newInstance();
  }

  private void sendReply(String procName, Queue dest, Response resp) throws JMSException,
      IOException  {

    log.info("WorkflowEngine.sendReply, this=" + hashCode() + ", dest=" + dest);
    QueueSender sender = session.createSender(dest);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream s = new ObjectOutputStream(out);
    s.writeObject (resp);
    s.flush();
    byte[] barr = out.toByteArray();

    BytesMessage m = session.createBytesMessage();
    m.writeBytes (barr);

    System.out.println ("Setting ReplyName to: " + procName);
    m.setStringProperty ("ReplyName", procName);

    sender.send(m);
    sender.close();
  }
}

