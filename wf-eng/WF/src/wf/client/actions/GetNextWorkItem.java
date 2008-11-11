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


package wf.client.actions;

import java.net.*;
import java.io.*;
import javax.jms.*;

import wf.client.*;
import wf.client.auth.*;
import wf.exceptions.XflowException;
import wf.jms.*;
import wf.model.WorkItem;
import xflow.common.*;

/**
 *  Gets the next work item (if available) from an inbox.
 *  This tool is a good way to peek into process inboxes. 
 *  <p/>
 *  Usage: java xflow.tools.GetNextWorkItem [workflow model name] [process name]
 *  <p/>  
 */
public class GetNextWorkItem {

    private String workflowName;
    private String procName;
    private WorkflowProcess wp;

    public GetNextWorkItem (String wfName, String pname) {
        workflowName = wfName;
        procName = pname;
    }

    public void start() throws XflowException {
        wp = new WorkflowProcess (workflowName, -1, procName, null, new User("rtan","rtan"));
        WorkItem wi = wp.getNextWorkItem();
        System.out.println ("Work Item: " + wi);

        System.out.print ("Complete this work item? [y/n]:");
        try {
           BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
           String s = in.readLine();
           if (s != null && s.equals("y")) {
               wp.completeWorkItem (wi);
	   } 
	} catch (Exception e) {         
	    System.out.println (e);
        }
    }

    public static void main (String[] args) throws XflowException, JMSException {
        String wfName = args[0];
        String procName = args[1];
        new GetNextWorkItem (wfName, procName).start();
    }
}
