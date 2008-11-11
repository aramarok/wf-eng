package wf.server.controller;

/**
 * User: kosta
 * Date: Jun 29, 2004
 * Time: 12:22:26 AM
 */
public class WorkItemRec {

  int workitemId;
  String  payload;
  String payloadType;

  public int getWorkitemId() {
    return workitemId;
  }

  public void setWorkitemId(int workitemId) {
    this.workitemId = workitemId;
  }

  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public String getPayloadType() {
    return payloadType;
  }

  public void setPayloadType(String payloadType) {
    this.payloadType = payloadType;
  }

}
