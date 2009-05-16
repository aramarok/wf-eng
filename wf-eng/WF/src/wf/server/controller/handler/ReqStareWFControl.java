package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqStareWF;
import wf.jms.model.ResStareWF;
import wf.model.StareWF;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqStareWFControl implements ControlCereri {

    public Raspuns handle(final Cerere cerere) {
	ReqStareWF req = (ReqStareWF) cerere;

	StareWF stare = null;
	int raspuns = 0;
	String mesaj = "OK";

	try {
	    stare = WorkflowProcessor.getInstance().getWorkflowState(
		    req.workflowId);
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}

	return new ResStareWF(raspuns, mesaj, stare);
    }
}
