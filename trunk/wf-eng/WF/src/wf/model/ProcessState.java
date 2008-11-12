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
import java.util.Date;


/**
 *  This is the process' current state - timeStarted, workitem being performed
 */
public class ProcessState implements Serializable {

    public Integer workflowId;
    public String processName;
    public Date timeStarted;
    public Integer  workItemId;

    /**
     *   Constructor
     */
    public ProcessState() {}


    /**
     *  Sets a workflow ID
     *
     *  @param id  the workflow ID
     */
    public void setWorkflowId (Integer id) {
        workflowId = id;
    }

    /**
     *  Gets a workflow ID
     *
     *  @return workflowId  the workflow ID
     */
    public Integer getWorkflowId () {
        return workflowId;
    }

    /**
     *  Sets a process name
     *
     *  @param n  the process name
     */
    public void setProcessName (String n) {
        processName = n;
    }

    /**
     *  Returns the process name
     *
     * 
     */
    public String getProcessName () {
        return processName;
    }

    /**
     *  Sets the time started
     *
     *  @param d  the start time
     */
    public void setTimeStarted (Date d) {
        timeStarted = d;
    }

    /**
     *  Gets the time started
     *
     *  @return d  the start time
     */
    public Date getTimeStarted () {
        return timeStarted;
    }

    /**
     *  Sets the work item ID
     *
     *  @param id  the work item ID
     */
    public void setWorkItemId (Integer id) {
        workItemId = id;
    }


    /**
     *  Gets the work item ID
     *
     *  @return the work item ID
     */
    public Integer getWorkItemId () {
        return workItemId;
    }

    /**
     *  Gets the string representation of this object
     *
     *  @return the string representation
     */
    public String toString () {
        String result = "";
        result += "Workflow ID: " + workflowId + "\n";
        result += "Process Name: " + processName + "\n";
        result += "Time Started: " + timeStarted + "\n";
        result += "Work Item ID: " + workItemId + "\n";
        return result;
    }
}
