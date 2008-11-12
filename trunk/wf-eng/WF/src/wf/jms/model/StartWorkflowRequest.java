

package wf.jms.model;

import wf.model.WorkItem;

public class StartWorkflowRequest extends Request {

    public String workflowName;
    public int version = -1;
    public WorkItem workItem;
}
