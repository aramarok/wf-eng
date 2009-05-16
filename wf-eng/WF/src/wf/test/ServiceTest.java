package wf.test;

import javax.xml.rpc.ParameterMode;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.axis.utils.Options;

public class ServiceTest {
    public static void main(final String[] args) throws Exception {

	@SuppressWarnings("unused")
	Options options = new Options(args);

	String endpoint = "http://localhost:8080/axis/services/WfService";

	String method = "getVariable";
	Service service = new Service();
	Call call = (Call) service.createCall();

	call.setTargetEndpointAddress(new java.net.URL(endpoint));
	call.setOperationName(method);
	call.addParameter("op1", XMLType.XSD_INT, ParameterMode.IN);
	call.addParameter("op2", XMLType.XSD_STRING, ParameterMode.IN);
	call.setReturnType(XMLType.XSD_STRING);

	Integer id = new Integer(1);
	String varName = "FOO";
	String ret = (String) call.invoke(new Object[] { id, varName });

	System.out.println("Got result : " + ret);
    }
}
