package wf.jms.model;

import wf.model.ItemModel;

public class ReqStartWF extends Cerere {

    private static final long serialVersionUID = 1L;

    public int version = -1;
    public String workflowName;
    public ItemModel workItem;
}
