package wf.client;

import wf.model.ItemModel;

public interface ListenerMesajInbox {

    public void onMessage(ItemModel workItem);
}
