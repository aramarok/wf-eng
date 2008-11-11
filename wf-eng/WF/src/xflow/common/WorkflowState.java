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
package xflow.common;

import java.util.*;
import java.io.*;

/**
 *  This class represents the workflow's state - its variables and participant process states
 */
public class WorkflowState implements Serializable {

  public Integer    id;
  public String     workflowName;
  public int        version;
  public boolean    isActive;
  public String     state;
  public String     initiator;
  public Date       timeStarted;
  public Date       timeEnded;
  public Map        variables;        // returns a hash table of Variables
  public List       activeProcesses;  // returns a list of ProcessStates for currently active processes

  /**
   *   Constructor
   */
  public WorkflowState() {
    variables = new HashMap();
    activeProcesses = new ArrayList();
  }


  public void setWorkflowId (Integer wfid) {
    id = wfid;
  }

  /**
   *  Gets a workflow ID
   *
   *  @return workflowId  the workflow ID
   */
  public Integer getWorkflowId () {
    return  id;
  }


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }

  /**
   *  Sets a workflow name
   *
   *  @param name  the workflow name
   */
  public void setWorkflowName (String name) {
    workflowName = name;
  }

  /**
   *  Returns the workflow name
   *
   *  @return workflowName  the workflow name
   */
  public String getWorkflowName () {
    return workflowName;
  }


  /**
   *  Sets a workflow state
   *
   *  @param s  the workflow state name
   */
  public void setState (String s) {
    state = s;
  }

  /**
   *  Returns the workflow state
   *
   *  @return workflowState  the workflow state name
   */
  public String getState () {
    return state;
  }


  /**
   *  Sets the initiator
   *
   *  @param s  the initiator (user)
   */
  public void setInitiator (String s) {
    initiator = s;
  }

  /**
   *  Returns the initiator
   *
   *  @return initiator  the user who initiated the workflow
   */
  public String getInitiator () {
    return initiator;
  }


  /**
   *  Sets the active status
   *
   *  @param b the active status
   */
  public void setIsActive (boolean b) {
    isActive = b;
  }

  /**
   *  Gets the active status
   *
   *  @return the active status
   */
  public boolean getIsActive () {
    return isActive;
  }

  /**
   *  Sets the time started
   *
   *  @param ts  the start time
   */
  public void setTimeStarted (Date ts) {
    timeStarted = ts;
  }

  /**
   *  Gets the time started
   *
   *  @return the start time
   */
  public Date getTimeStarted () {
    return timeStarted;
  }

  /**
   *  Sets the time ended
   *
   *  @param ts  the end time
   */
  public void setTimeEnded (Date ts) {
    timeEnded = ts;
  }

  /**
   *  Gets the time ended
   *
   *  @return the end time
   */
  public Date getTimeEnded () {
    return timeEnded;
  }

  /**
   *  Sets the variables hash map
   *
   *  @param v the variables hash map
   */
  public void setVariables ( Map v) {
    variables = v;
  }

  /**
   *  Gets the variables hash map
   *
   *  @return the variables hash map
   */
  public Map getVariables () {
    return variables;
  }

  /**
   *  Sets the List of ProcessState objects of active processes
   *
   *  @param v the List of ProcessState objects
   */
  public void setActiveProcesses (List v) {
    activeProcesses = v;
  }

  /**
   *  Gets the List of ProcessState objects of active processes
   *
   *  @return the List of ProcessState objects
   */
  public List getActiveProcesses () {
    return activeProcesses;
  }

  /**
   *  Gets the string representation of this object
   *
   *  @return the string representation
   */
  public String toString () {
    String result = "";
    result += "Workflow ID: " + id + "\n";
    result += "Workflow Name: " + workflowName + "\n";
    result += "Workflow Version: " + version + "\n";
    result += "IsActive: " + isActive + "\n";
    result += "State: " + state + "\n";
    result += "Initiator: " + initiator + "\n";
    result += "Time Started: " + timeStarted + "\n";
    result += "Time Ended: " + timeEnded + "\n";

    result += "Variables: \n";
    Iterator itr = variables.keySet().iterator();
    while (itr.hasNext()) {
      String key = (String) itr.next();
      Object val = variables.get(key);
      result += " Key: " + key;
      result += " Value: " + val + "\n";
    }

    result += "Processes: \n";
    for (int i = 0; i < activeProcesses.size(); i++) {
      ProcessState ps = (ProcessState) activeProcesses.get(i);
      result += ps.toString();
    }

    return result;
  }
}

