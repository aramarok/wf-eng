package wf.client;

import wf.model.WorkItem;

public interface InboxMessageListener {

	public void onMessage(WorkItem workItem);
}
