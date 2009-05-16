package wf.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

public class ItemModel implements Serializable {

    public static final String JAVA_OBJECT = "JavaObject";

    private static final long serialVersionUID = 1L;
    public static final String TXT = "TXT";
    public static final String XML = "XML";

    private Object payload;
    private String payloadType;
    @SuppressWarnings("unchecked")
    private HashMap properties = new HashMap();
    private Integer workflowId;
    private Integer workItemId;

    public ItemModel() {
    }

    public ItemModel(final Integer wid) {
	this.workItemId = wid;
	this.payload = null;
	this.payloadType = "";
    }

    public void _setPayloadType(final String pt) {
	this.payloadType = pt;
    }

    public Integer getId() {
	return this.workItemId;
    }

    public Object getPayload() {
	return this.payload;
    }

    public String getPayloadType() {
	return this.payloadType;
    }

    @SuppressWarnings("unchecked")
    public HashMap getProperties() {
	return this.properties;
    }

    public Object getProperty(final String key) {
	return this.properties.get(key);
    }

    public Integer getWorkflowId() {
	return this.workflowId;
    }

    @SuppressWarnings("unchecked")
    public ItemModel makeCopy() {
	ItemModel clonedWItem = new ItemModel();
	clonedWItem.payload = this.payload;
	clonedWItem.payloadType = this.payloadType;
	HashMap p = new HashMap(this.getProperties());
	clonedWItem.setProperties(p);
	return clonedWItem;
    }

    public void setId(final Integer id) {
	this.workItemId = id;
    }

    public void setPayload(final Object pload) {
	this.setPayloadAsJavaObject(pload);
    }

    public void setPayloadAsJavaObject(final Object payload) {
	this._setPayloadType(JAVA_OBJECT);
	this.payload = payload;
    }

    public void setPayloadTXT(final String s) {
	this._setPayloadType(TXT);
	this.payload = s;
    }

    public void setPayloadXML(final String s) {
	this._setPayloadType(XML);
	this.payload = s;
    }

    @SuppressWarnings("unchecked")
    public void setProperties(final HashMap m) {
	this.properties = m;
    }

    @SuppressWarnings("unchecked")
    public void setProperty(final String key, final Object value) {
	this.properties.put(key, value);
    }

    public void setWorkflowId(final Integer wfId) {
	this.workflowId = wfId;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String toString() {
	String str = "";
	str += "ItemModel Id: " + this.workItemId + "\n";
	str += "Workflow Id: " + this.workflowId + "\n";
	str += "Payload: " + this.payload + "\n";
	str += "Payload Type: " + this.payloadType + "\n";
	Iterator itr = this.properties.keySet().iterator();
	while (itr.hasNext()) {
	    String key = (String) itr.next();
	    Object val = this.properties.get(key);
	    str += "key = " + key;
	    str += " value = " + val + "\n";
	}
	return str;
    }

}
