package wf.jms.model;

public class ReqSetareVariabila extends Cerere {

    private static final long serialVersionUID = 1L;

    public String variableName;
    public Object variableValue;
    public Integer workflowId;

    public ReqSetareVariabila() {
    }

    public ReqSetareVariabila(final Integer workflowId,
	    final String variableName, final Object variableValue) {
	this.workflowId = workflowId;
	this.variableName = variableName;
	this.variableValue = variableValue;
    }

}
