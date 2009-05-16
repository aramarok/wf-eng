package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqAbortWF;
import wf.jms.model.ResAbortWF;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqAbortWFControl implements ControlCereri {

    public Raspuns handle(final Cerere cerere) {

	ReqAbortWF req = (ReqAbortWF) cerere;

	int raspuns = 0;
	String mesaj = "OK";

	try {
	    WorkflowProcessor.getInstance().abortWorkflow(req.workflowId,
		    req.utilizator.getName());
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}
	return new ResAbortWF(raspuns, mesaj);
    }

}
