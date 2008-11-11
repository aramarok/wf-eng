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

package xflow.webservice;

import xflow.common.*;
import xflow.security.*;
import xflow.client.*;
import java.util.*;

public class XflowService {
 
    public Integer startWorkflow (String workflowName, int version, WorkItem witem, User user) {
        Integer wfId = null;
        try {
            System.out.println ("**** workflowName: " + workflowName);
            System.out.println ("**** version: " + version);
            System.out.println ("**** workitem: " + witem);
            System.out.println ("**** user: " + user);
            if (version == -1) {
                wfId = WorkflowManager.startWorkflow (workflowName, witem, user);
            } else {
                wfId = WorkflowManager.startWorkflow (workflowName, version, witem, user);
	    }
            System.out.println ("Workflow Started");
	} catch (XflowException e) {
            // TBD Throw SOAP exception
            e.printStackTrace();
        }

        return wfId;
    }
    
    public void abortWorkflow (int workflowId, User user) {

        System.out.println ("Aborting workflow");
        try {
            WorkflowManager.abortWorkflow (new Integer(workflowId), user);
            System.out.println ("Workflow Aborted");
	} catch (XflowException e) {
            // TBD Throw SOAP exception
            e.printStackTrace();
        }
    }    

    public WorkflowState getWorkflowState (int workflowId, User user) {
        try {
            return WorkflowManager.getWorkflowState (new Integer(workflowId), user);
        } catch (XflowException e) {
            // TBD Throw SOAP exception
            e.printStackTrace();
            return null;
        }
    }

    public void setVariable (int workflowId, String variableName, Object variableValue, User user) {
        try {
            WorkflowManager.setVariable (new Integer(workflowId), variableName, variableValue, user);
        } catch (XflowException e) {
            // TBD Throw SOAP exception
            e.printStackTrace();
        }
    }

    public Object getVariable (int workflowId, String variableName, User user) {
        try {
            return WorkflowManager.getVariable (new Integer(workflowId), variableName, user);
        } catch (XflowException e) {
            // TBD Throw SOAP exception
            e.printStackTrace();
            return "ERROR";
        }
    }

    public List getActiveWorkflows(User user) {
        try {
            return WorkflowManager.getActiveWorkflows (user);
        } catch (XflowException e) {
            // TBD Throw SOAP exception
            e.printStackTrace();
            return null;
        }
    }

    public void deployModel (String xml, String type, User user) {
        try {
            WorkflowManager.deployModel (xml, type, user);
        } catch (XflowException e) {
            // TBD Throw SOAP exception
            e.printStackTrace();
        }
    }

    public List getWorkItems (String wfName, String processName, User user) {
        List v = null;
        try {
            WorkflowProcess wfp = new WorkflowProcess(wfName, -1, processName, null, user);
            v = wfp.getWorkItems();
        } catch (XflowException e) {
            // TBD Throw SOAP exception
            e.printStackTrace();
        }
        return v;
    }

    public WorkItem getNextWorkItem (String wfName, String processName, User user) {
        WorkItem wi = null;
        try {
            WorkflowProcess wfp = new WorkflowProcess(wfName, -1, processName, null, user);
            wi = wfp.getNextWorkItem();
        } catch (XflowException e) {
            // TBD Throw SOAP exception
            e.printStackTrace();
        }
        return wi;
    }

    public WorkItem getWorkItem (String wfName, String processName, int id, User user) {
        WorkItem wi = null;
        try {
            WorkflowProcess wfp = new WorkflowProcess(wfName, -1, processName, null, user);
            wi = wfp.getWorkItem(new Integer(id));
        } catch (XflowException e) {
            // TBD Throw SOAP exception
            e.printStackTrace();
        }
        return wi;
    }

    public void completeWorkItem (String wfName, String processName, WorkItem witem, User user) {
        try {
            WorkflowProcess wfp = new WorkflowProcess(wfName, -1, processName, null, user);
            wfp.completeWorkItem (witem);
        } catch (XflowException e) {
            // TBD Throw SOAP exception
            e.printStackTrace();
        }
    } 


    public String xxx () {
        return "XXX";
    }

    public List yyy() {
        List v = new ArrayList();
        v.add ("aaa");
        v.add ("bbb");
        return v;
    }
    
}
