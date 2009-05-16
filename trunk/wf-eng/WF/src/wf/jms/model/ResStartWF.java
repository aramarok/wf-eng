package wf.jms.model;

public class ResStartWF extends Raspuns {

    private static final long serialVersionUID = 1L;

    public Integer workflowId;

    public ResStartWF(final int cod, final int wfId) {
	super(cod);
	this.workflowId = new Integer(wfId);
    }

    public ResStartWF(final int cod, final String mesaj,
	    final Integer wfId) {
	super(cod, mesaj);
	this.workflowId = wfId;
    }
}
