package wf.jms.model;

public abstract class WorkflowProcessRequest extends Request {

	public String workflowName;
	public int workflowVersion = -1;
	public String processName;

}
