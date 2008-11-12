
package wf.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;


public class WorkItem implements Serializable {

  public static final String JAVA_OBJECT = "JavaObject";
  public static final String XML = "XML";
  public static final String TXT = "TXT";

  private Integer workItemId;
  private Integer workflowId;
  private Object payload;
  private String payloadType;
  private HashMap properties = new HashMap();

  
  public WorkItem() {
  }

  
  public WorkItem(Integer wid) {
    workItemId = wid;
    payload = null;
    payloadType = "";
  }

  
  public Integer getId() { return workItemId; }

  
  public void setId(Integer id) { workItemId = id; }

  
  public Integer getWorkflowId() { return workflowId; }

  
  public void setWorkflowId(Integer wfId ) { workflowId = wfId; }

  
  public void setPayload(Object pload) {
    setPayloadAsJavaObject( pload );
  }

  
  public Object getPayload () { return payload; }

  
  public void _setPayloadType (String pt) { payloadType = pt; }

  
  public String getPayloadType () { return payloadType; }

  
  public Object getProperty (String key) { return properties.get(key); }

  
  public void setProperty (String key, Object value) { properties.put (key, value); }

  
  public HashMap getProperties () { return properties; }

  
  public void setProperties(HashMap m) { properties = m; }

  
  public String toString() {
    String str = "";
    str += "WorkItem Id: " + workItemId + "\n";
    str += "Workflow Id: " + workflowId + "\n";
    str += "Payload: " + payload + "\n";
    str += "Payload Type: " + payloadType + "\n";
    Iterator itr = properties.keySet().iterator();
    while (itr.hasNext()) {
      String key = (String)itr.next();
      Object val = properties.get(key);
      str += "key = " + key;
      str += " value = " + val + "\n";
    }
    return str;
  }

  public void setPayloadAsJavaObject(Object payload) {
    _setPayloadType( JAVA_OBJECT );
    this.payload = payload ;
  }

  public void setPayloadXML(String s) {
    _setPayloadType( XML );
    this.payload = s ;
  }

  public void setPayloadTXT(String s) {
    _setPayloadType( TXT );
    this.payload  = s ;
  }

  public WorkItem makeCopy(){
    WorkItem clonedWItem = new WorkItem();
    clonedWItem.payload =  payload;
    clonedWItem.payloadType = payloadType;
    HashMap p = new HashMap( getProperties() );
    clonedWItem.setProperties (p);
    return clonedWItem;
  }

}
