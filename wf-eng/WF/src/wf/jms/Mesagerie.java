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
import wf.exceptions.ExceptieWF;
import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;

public class Mesagerie {

    private static Queue coadaPrimiri = null;
    private static Queue coadaWF = null;
    private static QueueConnection conexiuneCoada = null;
    private static QueueSession sesiuneCoada = null;

    static {

	try {

	    InitialContext context = new InitialContext();

	    Object tmp = context.lookup(AppConfig.getConnectionFactory());
	    QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
	    conexiuneCoada = qcf.createQueueConnection();

	    sesiuneCoada = conexiuneCoada.createQueueSession(false,
		    QueueSession.AUTO_ACKNOWLEDGE);
	    coadaPrimiri = (Queue) context.lookup(AppConfig.getOutboxQueue());

	    conexiuneCoada.start();

	    coadaWF = (Queue) context.lookup(AppConfig.getWfQueue());
	    OprireJMS shook = new OprireJMS();
	    Runtime.getRuntime().addShutdownHook(shook);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void close() throws JMSException {
	conexiuneCoada.close();
    }

    public static Raspuns sendRequest(final Cerere req) throws JMSException,
	    IOException, ClassNotFoundException, ExceptieWF {
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	ObjectOutputStream s = new ObjectOutputStream(out);

	s.writeObject(req);
	s.flush();
	byte[] baiti = out.toByteArray();
	String replyName = req.numeRaspuns;
	QueueReceiver receiver = sesiuneCoada.createReceiver(coadaPrimiri,
		"ReplyName in ('" + replyName + "')");
	QueueSender sender = sesiuneCoada.createSender(coadaWF);

	BytesMessage m = sesiuneCoada.createBytesMessage();
	m.writeBytes(baiti);
	m.setStringProperty("ReplyName", replyName);

	m.setJMSReplyTo(coadaPrimiri);
	sender.send(m);

	System.out.println("Receiver = " + receiver);
	System.out.println("Sender = " + sender);

	Message msg = receiver.receive(5000);
	Raspuns raspuns = null;
	if (msg != null) {
	    BytesMessage mesajBaiti = (BytesMessage) msg;
	    baiti = new byte[10000];
	    mesajBaiti.readBytes(baiti);
	    ByteArrayInputStream stream = new ByteArrayInputStream(baiti);
	    ObjectInputStream oStream = new ObjectInputStream(stream);
	    raspuns = (Raspuns) oStream.readObject();
	} else {
	    throw new ExceptieWF("raspunsul nu a venit in 5 secunde !");
	}
	return raspuns;
    }

}
