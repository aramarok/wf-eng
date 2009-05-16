package wf.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

public class Util {

    public static int generateUniqueIntId() {

	long tn = System.currentTimeMillis();
	String ts = "" + tn;
	String tss = ts.substring(6);
	Integer iObj = new Integer(tss);
	return iObj.intValue();
    }

    public static String generateUniqueStringId() {

	long tn = System.currentTimeMillis();
	String ts = "" + tn;
	String tss = ts.substring(6);
	return tss;
    }

    @SuppressWarnings("unchecked")
    public static Object getValue(final Map m, final String key) {
	Object res = m.get(key);
	if (res == null) {
	    res = m.get(key.toUpperCase());
	}
	return res;
    }

    public static Object objFromXML(final String xml) {
	ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
	XMLDecoder dec = new XMLDecoder(in);
	Object res = dec.readObject();
	dec.close();
	return res;
    }

    public static String objToXML(final Object o) {
	ByteArrayOutputStream out = new ByteArrayOutputStream(5000);
	XMLEncoder enc = new XMLEncoder(out);
	enc.writeObject(o);
	enc.close();
	return out.toString();
    }
}
