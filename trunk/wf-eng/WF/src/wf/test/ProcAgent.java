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
package wf.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.jms.JMSException;

import wf.client.InboxMessageListener;
import wf.client.WorkflowProcess;
import wf.client.auth.User;
import wf.exceptions.XflowException;
import wf.jms.JMSTopicConnection;
import wf.model.WorkItem;

public class ProcAgent implements InboxMessageListener {

    private String workflowName;
    private String procName;
    private WorkflowProcess wp;

    public ProcAgent (String wfName, String pname) {
        workflowName = wfName;
        procName = pname;
    }

    public void onMessage (WorkItem witem) {
        System.out.println ("Got a work item: " + witem);
        try {
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            System.out.print ("Enter value of PropA: ");
            String s = stdin.readLine();
            witem.setProperty ("PropA", s);
        } catch (Exception e) {
            System.out.println (e.getMessage());
        }

        System.out.println ("Completing work item");
        try {
            wp.completeWorkItem (witem);
        } catch (XflowException e) {
            e.printStackTrace();
        }
    }

    public void start() throws XflowException {
        wp = new WorkflowProcess (workflowName, -1, procName, this, new User("rtan","rtan"));
    }

    public static void main (String[] args) throws XflowException, JMSException {
        String wfName = args[0];
        String procName = args[1];
        JMSTopicConnection.initialize();
        new ProcAgent(wfName, procName).start();
    }
}
