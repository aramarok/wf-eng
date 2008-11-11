/*
* ====================================================================
*
* XFLOW - Process Management System
* Copyright (C) 2003 Rob Tan
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions, and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions, and the disclaimer that follows
*    these conditions in the documentation and/or other materials
*    provided with the distribution.
*
* 3. The name "XFlow" must not be used to endorse or promote products
*    derived from this software without prior written permission.  For
*    written permission, please contact rcktan@yahoo.com
*
* 4. Products derived from this software may not be called "XFlow", nor
*    may "XFlow" appear in their name, without prior written permission
*    from the XFlow Project Management (rcktan@yahoo.com)
*
* In addition, we request (but do not require) that you include in the
* end-user documentation provided with the redistribution and/or in the
* software itself an acknowledgement equivalent to the following:
*     "This product includes software developed by the
*      XFlow Project (http://xflow.sourceforge.net/)."
* Alternatively, the acknowledgment may be graphical using the logos
* available at http://xflow.sourceforge.net/
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE XFLOW AUTHORS OR THE PROJECT
* CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
*
* ====================================================================
* This software consists of voluntary contributions made by many
* individuals on behalf of the XFlow Project and was originally
* created by Rob Tan (rcktan@yahoo.com)
* For more information on the XFlow Project, please see:
*           <http://xflow.sourceforge.net/>.
* ====================================================================
*/
package wf.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

/**
 *  This class represents a work item that gets routed to a process.
 */
public class WorkItem implements Serializable {

  public static final String JAVA_OBJECT = "JavaObject";
  public static final String XML = "XML";
  public static final String TXT = "TXT";

  private Integer workItemId;
  private Integer workflowId;
  private Object payload;
  private String payloadType;
  private HashMap properties = new HashMap();

  /**
   *  Constructor
   */
  public WorkItem() {
  }

  /**
   *  Constructor
   *
   *  @param wid a work item ID
   */
  public WorkItem(Integer wid) {
    workItemId = wid;
    payload = null;
    payloadType = "";
  }

  /**
   *  Gets the work item ID
   *
   *  @return the work item ID
   */
  public Integer getId() { return workItemId; }

  /**
   *  Sets the work item ID
   *
   *  @param id the work item ID
   */
  public void setId(Integer id) { workItemId = id; }

  /**
   *  Gets the workflow instance ID
   *
   *  @return the workflow instance ID
   */
  public Integer getWorkflowId() { return workflowId; }

  /**
   *  Sets the workflow instance ID
   *
   *  @param wfId the workflow instance ID
   */
  public void setWorkflowId(Integer wfId ) { workflowId = wfId; }

  /**
   *  Sets the payload of this work item.
   *
   *  @param pload the payload
   */
  public void setPayload(Object pload) {
    setPayloadAsJavaObject( pload );
  }

  /**
   *  Gets the payload of this work item.
   *
   *  @return the payload
   */
  public Object getPayload () { return payload; }

  /**
   *  Sets the payload type of this work item.
   *
   *  @param pt the payload type - JAVA_OBJECT or XML
   */
  public void _setPayloadType (String pt) { payloadType = pt; }

  /**
   *  Gets the payload type of this work item.
   *
   *  @return the payload type - JAVA_OBJECT or XML
   */
  public String getPayloadType () { return payloadType; }

  /**
   *  Gets a work item's property
   *
   *  @param key   the property name
   *  @return the property value
   */
  public Object getProperty (String key) { return properties.get(key); }

  /**
   *  Sets a property on a work item
   *
   *  @param key   the property name
   *  @param value the property value - must be serializable
   *
   */
  public void setProperty (String key, Object value) { properties.put (key, value); }

  /**
   *  Gets a work item's property hash map
   *
   *  @return the property hash map
   */
  public HashMap getProperties () { return properties; }

  /**
   *  Sets a work item's property hash map
   *
   *  @param m the property hash map
   */
  public void setProperties(HashMap m) { properties = m; }

  /**
   *  Gets the string representation of this object
   *
   *  @return the string representation
   */
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
