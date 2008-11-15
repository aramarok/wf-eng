package wf.server.controller;

import org.apache.log4j.Logger;
import wf.cfg.AppConfig;
import wf.client.auth.Authenticator;
import wf.client.auth.UserAuth;
import wf.db.Persistence;
import wf.jms.model.Request;
import wf.jms.model.Response;
import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.*;

public class WorkflowEngine implements MessageDrivenBean, MessageListener {

	private static final long serialVersionUID = 1L;

	public static final String FLOW_TYPE_WF = "WF";

	private static Logger log = Logger.getLogger(WorkflowEngine.class);

	private RequestHandlerFactory requestHandlerFactory = new RequestHandlerFactory();

	private Authenticator authenticator = null;

	private MessageDrivenContext ctx = null;
	private QueueConnection conn;
	private QueueSession session;

	public WorkflowEngine() {
		log.info("WorkflowEngine.constructor, this=" + hashCode());
		Persistence.init();
	}

	public void setMessageDrivenContext(MessageDrivenContext ctx) {
		this.ctx = ctx;
		log.info("WorkflowEngine.setMessageDrivenContext, this=" + hashCode());
	}

	public void ejbCreate() {
		log.info("WorkflowEngine.ejbCreate, this=" + hashCode());
		try {
			setupPTP();
		} catch (Exception e) {
			log.error("Failed to init WorkflowEngine", e);
			throw new EJBException("Failed to init WorkflowEngine", e);
		}
	}

	public void ejbRemove() {

		log.info("WorkflowEngine.ejbRemove, this=" + hashCode());
		ctx = null;
		try {
			if (session != null) {
				session.close();
			}
			if (conn != null) {
				conn.close();
			}
		} catch (JMSException e) {
			log.error("ejbRemove error", e);
		}
	}

	public void onMessage(Message msg) {

		log.info("WorkflowEngine.onMessage, this=" + hashCode());
		try {
			Queue dest = (Queue) msg.getJMSReplyTo();
			log.info("Reply queue is: " + dest.getQueueName());
			String procName = msg.getStringProperty("ReplyName");
			log.info("procName = " + procName);

			BytesMessage bytesMessage = (BytesMessage) msg;
			byte[] barr = new byte[10000];
			bytesMessage.readBytes(barr);

			ByteArrayInputStream in = new ByteArrayInputStream(barr);
			ObjectInputStream sin = new ObjectInputStream(in);
			Request request = (Request) sin.readObject();
			String userName = request.user.getName();
			String password = request.user.getPassword();
			log.info("userName = " + userName + " password = " + password);
			if (authenticator.authenticate(userName, password) == false) {
				Response authFailedResponse = new Response();
				authFailedResponse.responseCode = Response.FAILURE;
				authFailedResponse.message = "Authentication failed for "
						+ userName;
				sendReply(procName, dest, authFailedResponse);
			} else {
				Response response = handle(request);
				sendReply(procName, dest, response);
			}
		} catch (Throwable t) {
			log.error("onMessage error", t);
		}

	}

	public Response handle(Request req) {
		RequestHandler rH = requestHandlerFactory.getHandlerFor(req);
		return rH.handle(req);

	}

	private void setupPTP() throws JMSException, NamingException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException {

		InitialContext iniCtx = new InitialContext();
		Object tmp = iniCtx.lookup(AppConfig.getConnectionFactory());
		QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
		conn = qcf.createQueueConnection();
		session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
		conn.start();

		String authClassName = (String) iniCtx
				.lookup("java:comp/env/authenticator");
		if (authClassName == null || authClassName.length() == 0) {
			log.info("Authenticator was not supplied, use default. ");
			authClassName = UserAuth.class.getName();
		}
		log.info("Authenticator is: " + authClassName);
		authenticator = (Authenticator) Class.forName(authClassName)
				.newInstance();
	}

	private void sendReply(String procName, Queue dest, Response resp)
			throws JMSException, IOException {

		log.info("WorkflowEngine.sendReply, this=" + hashCode() + ", dest="
				+ dest);
		QueueSender sender = session.createSender(dest);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream s = new ObjectOutputStream(out);
		s.writeObject(resp);
		s.flush();
		byte[] barr = out.toByteArray();

		BytesMessage m = session.createBytesMessage();
		m.writeBytes(barr);

		System.out.println("Setting ReplyName to: " + procName);
		m.setStringProperty("ReplyName", procName);

		sender.send(m);
		sender.close();
	}
}
