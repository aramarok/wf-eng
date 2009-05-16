package wf.jms.model;

public class ReqUrmWI extends ReqProcesareWF {

    private static final long serialVersionUID = 1L;

    public ReqUrmWI() {
    }

    public ReqUrmWI(final String numeWF, final String numeProces) {
	this.workflowName = numeWF;
	this.processName = numeProces;
    }

}
