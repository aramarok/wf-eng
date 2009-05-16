package wf.jms;

public class OprireJMS extends Thread {

    public OprireJMS() {
    }

    @Override
    public void run() {
	try {
	    ConexiuneTopicJMS.close();
	    Mesagerie.close();
	} catch (Exception e) {
	}
    }
}
