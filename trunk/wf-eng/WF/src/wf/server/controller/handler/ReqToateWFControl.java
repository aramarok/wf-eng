package wf.server.controller.handler;

import java.util.List;
import wf.jms.model.Cerere;
import wf.jms.model.Raspuns;
import wf.jms.model.ResToateWF;
import wf.server.controller.ControlCereri;
import wf.server.controller.WorkflowProcessor;

public class ReqToateWFControl implements ControlCereri {

    @SuppressWarnings("unchecked")
    public Raspuns handle(final Cerere cerere) {

	int raspuns = 0;
	String mesaj = "OK";
	List wfUri = null;

	try {
	    wfUri = WorkflowProcessor.getInstance().getAllWorkflows();
	    raspuns = Raspuns.SUCCES;
	} catch (Exception e) {
	    raspuns = Raspuns.EROARE;
	    e.printStackTrace();
	    mesaj = e.getMessage();
	}
	return new ResToateWF(raspuns, mesaj, wfUri);
    }
}
