package wf.client;

import wf.model.ItemModel;

public interface InboxMessageListener {

    public void onMessage(ItemModel workItem);
}
