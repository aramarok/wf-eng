


package wf.test;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.utils.Options;

import wf.client.auth.User;
import wf.model.WorkItem;

public class SetGetVariableWS {
   public static void main(String [] args) throws Exception {

       Options options = new Options(args);
       
       String endpoint = "http://localhost:8080/axis/services/XflowService";
       
       Service  service = new Service();

       Call     call    = (Call) service.createCall();
       QName    qn      = new QName( "urn:XflowService", "Integer" );
       call.registerTypeMapping(Integer.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Integer.class, qn),
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Integer.class, qn));

       qn      = new QName( "urn:XflowService", "Integer" );
       call.registerTypeMapping(Integer.class, qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(Integer.class, qn),
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(Integer.class, qn));

       QName   qn1      = new QName( "urn:XflowService", "WorkItem" );
       call.registerTypeMapping(WorkItem.class, qn1,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(WorkItem.class, qn1),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(WorkItem.class, qn1));        

       QName   qn2      = new QName( "urn:XflowService", "User" );
       call.registerTypeMapping(User.class, qn2,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(User.class, qn2),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(User.class, qn2));        

       call.setTargetEndpointAddress( new java.net.URL(endpoint) );
       call.setOperationName( "setVariable" );
       call.addParameter( "op1", XMLType.XSD_INT, ParameterMode.IN );
       call.addParameter( "op2", XMLType.XSD_STRING, ParameterMode.IN );
       call.addParameter( "op3", XMLType.XSD_STRING, ParameterMode.IN );
       call.addParameter( "op4", qn2, ParameterMode.IN );
       Integer wfId = new Integer(args[0]);
       String varName = args[1];
       String varVal = args[2];
       User user = new User ("rtan", "rtan");

       call.invoke( new Object [] { wfId, varName, varVal, user });

       call    = (Call) service.createCall();
       call.registerTypeMapping(User.class, qn2,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(User.class, qn2),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(User.class, qn2));        

       call.setTargetEndpointAddress( new java.net.URL(endpoint) );
       call.setOperationName( "getVariable" );
       call.addParameter( "op1", XMLType.XSD_INT, ParameterMode.IN );
       call.addParameter( "op2", XMLType.XSD_STRING, ParameterMode.IN );
       call.addParameter( "op3", qn2, ParameterMode.IN );
       call.setReturnType( org.apache.axis.encoding.XMLType.XSD_STRING );

       String result = (String)call.invoke(new Object [] { wfId, varName, user });
       System.out.println ("Result: " + result);
   }
}
