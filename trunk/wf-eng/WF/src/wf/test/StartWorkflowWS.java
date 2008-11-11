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

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;

import wf.client.auth.User;
import wf.model.WorkItem;

public class StartWorkflowWS {
   public static void main(String [] args) throws Exception {

       String endpoint = "http://localhost:8080/axis/services/XflowService";
       String method = "startWorkflow";
       Service service = new Service();
       Call call = (Call) service.createCall();

       QName qn = new QName( "urn:XflowService", "Integer" );

       call.registerTypeMapping(Integer.class, qn,
                                new BeanSerializerFactory(Integer.class, qn),
                                new BeanDeserializerFactory(Integer.class, qn));

       qn = new QName( "urn:XflowService", "Integer" );
       call.registerTypeMapping(Integer.class, qn,
                                new BeanSerializerFactory(Integer.class, qn),
                                new BeanDeserializerFactory(Integer.class, qn));

       QName qn1 = new QName( "urn:XflowService", "WorkItem" );
       call.registerTypeMapping(WorkItem.class, qn1,
                                new BeanSerializerFactory(WorkItem.class, qn1),        
                                new BeanDeserializerFactory(WorkItem.class, qn1));        

       QName qn2 = new QName( "urn:XflowService", "User" );
       call.registerTypeMapping(User.class, qn2,
                                new BeanSerializerFactory(User.class, qn2),        
                                new BeanDeserializerFactory(User.class, qn2));        

       call.setTargetEndpointAddress( new java.net.URL(endpoint) );
       call.setOperationName( method );
       call.addParameter( "op1", XMLType.XSD_STRING, ParameterMode.IN );
       call.addParameter( "op2", XMLType.XSD_INT, ParameterMode.IN );
       call.addParameter( "op3", qn1, ParameterMode.IN );
       call.addParameter( "op4", qn2, ParameterMode.IN );

       String workflowName = args[0];
       Integer version = new Integer(-1);
       WorkItem witem = new WorkItem ();
       witem.setPayload (new Integer(1));
       User user = new User ("foo", "foo");
       call.invoke( new Object [] { workflowName, version, witem, user });
       
       System.out.println("Workflow started.");
   }
}
