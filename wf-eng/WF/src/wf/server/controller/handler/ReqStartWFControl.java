package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqStartWF;
import wf.jms.model.ResStartWF;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqStartWFControl implements ControlCereri {

    public Raspuns handle(final Cerere cerere) {
	ReqStartWF req = (ReqStartWF) cerere;

	int raspuns = 0;
	Integer wfId = null;
	String numeUtilizator = req.utilizator.getName();
	String mesaj = "OK";

	try {
	    wfId = WorkflowProcessor.getInstance()
		    .startWorkflow(req.workflowName, req.version, req.workItem,
			    numeUtilizator);
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}
	return new ResStartWF(raspuns, mesaj, wfId);
    }

}
