package wf.jms.model;

public class GetNodeByNameRequest extends Request {

	private static final long serialVersionUID = 1L;
	
	public String workflowName;
	public int version;
	public String nodeName;

}
