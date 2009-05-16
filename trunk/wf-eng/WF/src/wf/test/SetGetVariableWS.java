package wf.test;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.utils.Options;
import wf.client.auth.Utilizator;
import wf.model.ItemModel;

public class SetGetVariableWS {
    public static void main(final String[] args) throws Exception {

	@SuppressWarnings("unused")
	Options options = new Options(args);

	String endpoint = "http://localhost:8080/axis/services/WfService";

	Service service = new Service();

	Call call = (Call) service.createCall();
	QName qn = new QName("urn:WfService", "Integer");
	call.registerTypeMapping(Integer.class, qn,
		new org.apache.axis.encoding.ser.BeanSerializerFactory(
			Integer.class, qn),
		new org.apache.axis.encoding.ser.BeanDeserializerFactory(
			Integer.class, qn));

	qn = new QName("urn:WfService", "Integer");
	call.registerTypeMapping(Integer.class, qn,
		new org.apache.axis.encoding.ser.BeanSerializerFactory(
			Integer.class, qn),
		new org.apache.axis.encoding.ser.BeanDeserializerFactory(
			Integer.class, qn));

	QName qn1 = new QName("urn:WfService", "ItemModel");
	call.registerTypeMapping(ItemModel.class, qn1,
		new org.apache.axis.encoding.ser.BeanSerializerFactory(
			ItemModel.class, qn1),
		new org.apache.axis.encoding.ser.BeanDeserializerFactory(
			ItemModel.class, qn1));

	QName qn2 = new QName("urn:WfService", "Utilizator");
	call.registerTypeMapping(Utilizator.class, qn2,
		new org.apache.axis.encoding.ser.BeanSerializerFactory(
			Utilizator.class, qn2),
		new org.apache.axis.encoding.ser.BeanDeserializerFactory(
			Utilizator.class, qn2));

	call.setTargetEndpointAddress(new java.net.URL(endpoint));
	call.setOperationName("setVariable");
	call.addParameter("op1", XMLType.XSD_INT, ParameterMode.IN);
	call.addParameter("op2", XMLType.XSD_STRING, ParameterMode.IN);
	call.addParameter("op3", XMLType.XSD_STRING, ParameterMode.IN);
	call.addParameter("op4", qn2, ParameterMode.IN);
	Integer wfId = new Integer(args[0]);
	String varName = args[1];
	String varVal = args[2];
	Utilizator user = new Utilizator("utilizator", "password");

	call.invoke(new Object[] { wfId, varName, varVal, user });

	call = (Call) service.createCall();
	call.registerTypeMapping(Utilizator.class, qn2,
		new org.apache.axis.encoding.ser.BeanSerializerFactory(
			Utilizator.class, qn2),
		new org.apache.axis.encoding.ser.BeanDeserializerFactory(
			Utilizator.class, qn2));

	call.setTargetEndpointAddress(new java.net.URL(endpoint));
	call.setOperationName("getVariable");
	call.addParameter("op1", XMLType.XSD_INT, ParameterMode.IN);
	call.addParameter("op2", XMLType.XSD_STRING, ParameterMode.IN);
	call.addParameter("op3", qn2, ParameterMode.IN);
	call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);

	String result = (String) call
		.invoke(new Object[] { wfId, varName, user });
	System.out.println("Result: " + result);
    }
}
