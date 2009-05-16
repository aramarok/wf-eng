package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqNodDupaNume;
import wf.jms.model.ResNodDupaNume;
import wf.model.Nod;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqNodDupaNumeControl implements ControlCereri {

    public Raspuns handle(final Cerere cerere) {
	ReqNodDupaNume req = (ReqNodDupaNume) cerere;

	int raspuns = 0;
	String mesaj = "OK";
	Nod nod = null;

	try {
	    nod = WorkflowProcessor.getInstance().getNodeByName(
		    req.workflowName, req.version, req.nodeName);
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}
	return new ResNodDupaNume(raspuns, mesaj, nod);
    }
}
