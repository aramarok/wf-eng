package wf.server.controller;

public class WorkItemRec {

    String payload;
    String payloadType;
    int workitemId;

    public String getPayload() {
	return this.payload;
    }

    public String getPayloadType() {
	return this.payloadType;
    }

    public int getWorkitemId() {
	return this.workitemId;
    }

    public void setPayload(final String payload) {
	this.payload = payload;
    }

    public void setPayloadType(final String payloadType) {
	this.payloadType = payloadType;
    }

    public void setWorkitemId(final int workitemId) {
	this.workitemId = workitemId;
    }

}
