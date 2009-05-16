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
import wf.exceptions.ExceptieWF;

public class PublisherJMS {

    private static boolean trimite = true;

    public static boolean isSendingEnabled() {
	return trimite;
    }

    @SuppressWarnings("unchecked")
    public static void send(final String numeTopic, final byte[] mesaj,
	    final List lista) throws ExceptieWF {
	send(numeTopic, null, mesaj, lista);
    }

    @SuppressWarnings("unchecked")
    private static void send(final String numeTopic, final String sir,
	    final byte[] mesaj, final List lista) throws ExceptieWF {
	if (!trimite) {
	    return;
	}
	try {
	    InitialContext context = ConexiuneTopicJMS.getInitialContext();
	    TopicConnection conexiune = ConexiuneTopicJMS.getConnection();
	    Topic topic = (Topic) context.lookup(numeTopic);
	    TopicSession sesiune = conexiune.createTopicSession(false,
		    TopicSession.AUTO_ACKNOWLEDGE);
	    TopicPublisher publisher = sesiune.createPublisher(topic);
	    Message m = null;
	    if (mesaj != null) {
		BytesMessage msj = sesiune.createBytesMessage();
		msj.writeBytes(mesaj);
		m = msj;
	    } else if (sir != null) {
		TextMessage msj = sesiune.createTextMessage(sir);
		m = msj;
	    }
	    if (lista != null) {
		for (int i = 0; i < lista.size(); i++) {
		    ProprietatiMesaje mp = (ProprietatiMesaje) lista.get(i);
		    m.setStringProperty(mp.name, mp.value);
		}
	    }
	    publisher.publish(m);
	    publisher.close();
	} catch (Exception e) {
	    throw new ExceptieWF(numeTopic, e);
	}
    }

    @SuppressWarnings("unchecked")
    public static void send(final String numeTopic, final String s,
	    final List lista) throws ExceptieWF {
	send(numeTopic, s, null, lista);
    }

    public static void setSendingEnabled(final boolean daOriBa) {
	trimite = daOriBa;
    }
}
