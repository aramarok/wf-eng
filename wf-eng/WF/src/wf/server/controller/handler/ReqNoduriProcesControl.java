package wf.server.controller.handler;

import java.util.List;
import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqNoduriProces;
import wf.jms.model.ResNoduriProces;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqNoduriProcesControl implements ControlCereri {

    @SuppressWarnings("unchecked")
    public Raspuns handle(final Cerere cerere) {
	ReqNoduriProces req = (ReqNoduriProces) cerere;

	int raspuns = 0;
	String mesaj = "OK";
	List noduri = null;

	try {
	    noduri = WorkflowProcessor.getInstance().getProcessNodes(
		    req.workflowId);
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}
	return new ResNoduriProces(raspuns, mesaj, noduri);
    }
}
