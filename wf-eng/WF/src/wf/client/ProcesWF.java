package wf.client;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import org.apache.log4j.Logger;
import wf.cfg.AppConfig;
import wf.client.auth.Utilizator;
import wf.exceptions.ExceptieWF;
import wf.jms.InregistrareJMS;
import wf.jms.ConexiuneTopicJMS;
import wf.jms.Mesagerie;
import wf.jms.model.ReqCompleteWI;
import wf.jms.model.ResCompleteWI;
import wf.jms.model.ReqUrmWI;
import wf.jms.model.ResUrmWI;
import wf.jms.model.ReqWI;
import wf.jms.model.ResWI;
import wf.jms.model.ReqWIs;
import wf.jms.model.ResWIs;
import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqValidareProces;
import wf.jms.model.ResValidareProces;
import wf.model.ItemModel;
import wf.util.Util;

public class ProcesWF implements MessageListener {

    private static Logger log = Logger.getLogger(ProcesWF.class);

    private static Raspuns sendRequest(final Cerere req) throws ExceptieWF {

	req.numeRaspuns = Util.generateUniqueStringId();
	try {
	    Raspuns resp = Mesagerie.sendRequest(req);
	    if (resp.codRaspuns != Raspuns.SUCCES) {
		throw new ExceptieWF(resp.mesaj);
	    }
	    return resp;
	} catch (Exception t) {
	    throw new ExceptieWF(t);
	}
    }

    private final InboxMessageListener mlistener;
    private final String procName;
    @SuppressWarnings("unused")
    private InregistrareJMS subscriber;
    private final Utilizator user;

    private final String workflowName;

    private final int workflowVersion;

    public ProcesWF(final String wfName, final int wfVersion,
	    final String processName, final InboxMessageListener listener,
	    final Utilizator user) throws ExceptieWF {

	try {
	    ConexiuneTopicJMS.initialize();
	} catch (JMSException e) {
	    throw new ExceptieWF(e);
	}

	this.workflowName = wfName;
	this.workflowVersion = wfVersion;
	this.procName = processName;
	this.mlistener = listener;
	this.user = user;
	ReqValidareProces req = new ReqValidareProces();
	req.workflowName = this.workflowName;
	req.workflowVersion = this.workflowVersion;
	req.processName = this.procName;
	req.utilizator = user;

	ResValidareProces resp = (ResValidareProces) sendRequest(req);
	if (!resp.ok) {
	    throw new ExceptieWF(
		    "Unrecognized process name in specified workflow.");
	}
	if (listener != null) {
	    this.subscriber = new InregistrareJMS(this,
		    AppConfig.getInboxTopic(), "ProcessName in ('"
			    + this.workflowName + this.procName + "')");
	}
    }

    public ResCompleteWI completeWorkItem(final ItemModel workItem)
	    throws ExceptieWF {

	ReqCompleteWI req = new ReqCompleteWI();
	req.workflowName = this.workflowName;
	req.workflowVersion = this.workflowVersion;
	req.processName = this.procName;
	req.utilizator = this.user;
	req.workItem = workItem;
	ResCompleteWI resp = (ResCompleteWI) sendRequest(req);
	return resp;
    }

    public ItemModel getNextWorkItem() throws ExceptieWF {

	ReqUrmWI req = new ReqUrmWI();
	req.workflowName = this.workflowName;
	req.workflowVersion = this.workflowVersion;
	req.processName = this.procName;
	req.utilizator = this.user;
	ResUrmWI resp = (ResUrmWI) sendRequest(req);
	return resp.workItem;
    }

    public ItemModel getWorkItem(final Integer workItemId) throws ExceptieWF {

	ReqWI req = new ReqWI();
	req.workflowName = this.workflowName;
	req.workflowVersion = this.workflowVersion;
	req.processName = this.procName;
	req.utilizator = this.user;
	req.workItemId = workItemId;
	ResWI resp = (ResWI) sendRequest(req);
	return resp.workItem;
    }

    @SuppressWarnings("unchecked")
    public List getWorkItems() throws ExceptieWF {

	ReqWIs req = new ReqWIs();
	req.workflowName = this.workflowName;
	req.workflowVersion = this.workflowVersion;
	req.processName = this.procName;
	req.utilizator = this.user;
	ResWIs resp = (ResWIs) sendRequest(req);
	return resp.workItems;
    }

    public void onMessage(final Message msg) {

	ItemModel workItem = null;

	try {
	    BytesMessage bytesMessage = (BytesMessage) msg;
	    byte[] barr = new byte[10000];
	    bytesMessage.readBytes(barr);

	    ByteArrayInputStream in = new ByteArrayInputStream(barr);
	    ObjectInputStream sin = new ObjectInputStream(in);
	    workItem = (ItemModel) sin.readObject();
	} catch (Throwable t) {
	    log.error("onMessage error", t);
	}

	this.mlistener.onMessage(workItem);
    }
}
