package wf.server.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import wf.cfg.AppConfig;
import wf.client.auth.AutentificareUtilizator;
import wf.client.auth.Autentificator;
import wf.db.Persistence;
import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;

public class WorkflowEngine implements MessageDrivenBean, MessageListener {

    public static final String FLOW_TYPE_WF = "WF";

    private static Logger log = Logger.getLogger(WorkflowEngine.class);

    private static final long serialVersionUID = 1L;

    private Autentificator authenticator = null;

    private QueueConnection conn;

    @SuppressWarnings("unused")
    private MessageDrivenContext ctx = null;
    private final RequestHandlerFactory requestHandlerFactory = new RequestHandlerFactory();
    private QueueSession session;

    public WorkflowEngine() {
	log.info("WorkflowEngine.constructor, this=" + this.hashCode());
	Persistence.init();
    }

    public void ejbCreate() {
	log.info("WorkflowEngine.ejbCreate, this=" + this.hashCode());
	try {
	    this.setupPTP();
	} catch (Exception e) {
	    log.error("Failed to init WorkflowEngine", e);
	    throw new EJBException("Failed to init WorkflowEngine", e);
	}
    }

    public void ejbRemove() {

	log.info("WorkflowEngine.ejbRemove, this=" + this.hashCode());
	this.ctx = null;
	try {
	    if (this.session != null) {
		this.session.close();
	    }
	    if (this.conn != null) {
		this.conn.close();
	    }
	} catch (JMSException e) {
	    log.error("ejbRemove error", e);
	}
    }

    public Raspuns handle(final Cerere req) {
	ControlCereri rH = this.requestHandlerFactory.getHandlerFor(req);
	return rH.handle(req);

    }

    public void onMessage(final Message msg) {

	log.info("WorkflowEngine.onMessage, this=" + this.hashCode());
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
	    Cerere request = (Cerere) sin.readObject();
	    String userName = request.utilizator.getName();
	    String password = request.utilizator.getPassword();
	    log.info("userName = " + userName + " password = " + password);
	    if (this.authenticator.authenticateUser(userName, password) == false) {
		Raspuns authFailedResponse = new Raspuns();
		authFailedResponse.codRaspuns = Raspuns.EROARE;
		authFailedResponse.mesaj = "Authentication failed for "
			+ userName;
		this.sendReply(procName, dest, authFailedResponse);
	    } else {
		Raspuns response = this.handle(request);
		this.sendReply(procName, dest, response);
	    }
	} catch (Throwable t) {
	    log.error("onMessage error", t);
	}

    }

    private void sendReply(final String procName, final Queue dest,
	    final Raspuns resp) throws JMSException, IOException {

	log.info("WorkflowEngine.sendReply, this=" + this.hashCode()
		+ ", dest=" + dest);
	QueueSender sender = this.session.createSender(dest);

	ByteArrayOutputStream out = new ByteArrayOutputStream();
	ObjectOutputStream s = new ObjectOutputStream(out);
	s.writeObject(resp);
	s.flush();
	byte[] barr = out.toByteArray();

	BytesMessage m = this.session.createBytesMessage();
	m.writeBytes(barr);

	System.out.println("Setting ReplyName to: " + procName);
	m.setStringProperty("ReplyName", procName);

	sender.send(m);
	sender.close();
    }

    public void setMessageDrivenContext(final MessageDrivenContext ctx) {
	this.ctx = ctx;
	log.info("WorkflowEngine.setMessageDrivenContext, this="
		+ this.hashCode());
    }

    private void setupPTP() throws JMSException, NamingException,
	    ClassNotFoundException, InstantiationException,
	    IllegalAccessException {

	InitialContext iniCtx = new InitialContext();
	Object tmp = iniCtx.lookup(AppConfig.getConnectionFactory());
	QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
	this.conn = qcf.createQueueConnection();
	this.session = this.conn.createQueueSession(false,
		QueueSession.AUTO_ACKNOWLEDGE);
	this.conn.start();

	String authClassName = (String) iniCtx
		.lookup("java:comp/env/authenticator");
	if ((authClassName == null) || (authClassName.length() == 0)) {
	    log.info("Autentificator was not supplied, use default. ");
	    authClassName = AutentificareUtilizator.class.getName();
	}
	log.info("Autentificator is: " + authClassName);
	this.authenticator = (Autentificator) Class.forName(authClassName)
		.newInstance();
    }
}
