package wf.server.controller.handler;

import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ReqSetareVariabila;
import wf.jms.model.ResSetareVariabila;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqSetareVariabilaControl implements ControlCereri {

    public Raspuns handle(final Cerere cerere) {
	ReqSetareVariabila req = (ReqSetareVariabila) cerere;

	int raspuns = 0;
	String mesaj = "OK";

	try {
	    WorkflowProcessor.getInstance().setVariable(req.workflowId,
		    req.variableName, req.variableValue);
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}
	return new ResSetareVariabila(raspuns, mesaj);
    }
}
