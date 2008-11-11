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

import java.util.List;

import wf.client.WorkflowManager;
import wf.client.auth.User;
import wf.model.Destination;
import wf.model.Node;

/**
 *  Gets a node in a workflow model by node name.
 *  This tool is useful for introspecting nodes in a workflow model which have been 
 *  deployed in the server. The output is sent to STDOUT.
 *  <p/>
 *  Usage: java xflow.tools.GetNodeByNodeName [workflow model name] [version] [node name]
 *  <p/>
 *  If the latest version is desired, use -1 for version.
 *    
 */
public class GetNodeByNodeName {

    public static void main (String[] args) throws Exception {

        String workflowName = args[0];
        String versionStr = args[1];
        int version = new Integer(versionStr).intValue();
        String nodeName = args[2];

        Node node = WorkflowManager.getNodeByName (workflowName, version, nodeName, 
                                                  new User("foo", "foo"));
        node.print();
        System.out.println("");
        System.out.println ("Nodes which transition into this node: ");
        List fromNodes = node.getFromNodes();
        for (int i = 0; i < fromNodes.size(); i++) {
            Node fnode = (Node) fromNodes.get(i);
            fnode.print();
        }
        System.out.println ("");
        System.out.println ("Nodes which this node transitions to: ");
        List destinations = node.getDestinations();
        for (int i = 0; i < destinations.size(); i++) {
            Destination d = (Destination)destinations.get(i);
            d.node.print();
            if (d.rule != null) {
		System.out.println ("rule: " + d.rule);
	    }
        }
    }
}
