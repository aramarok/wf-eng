package wf.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

public class Preferinte {

    @SuppressWarnings("unchecked")
    private static Map preferences = new Hashtable();

    @SuppressWarnings("unchecked")
    public static synchronized Preferinte getInstance(final String fileName)
	    throws IOException {
	Preferinte p = (Preferinte) preferences.get(fileName);
	if (p == null) {
	    p = new Preferinte(fileName);
	    preferences.put(fileName, p);
	}
	return p;

    }

    private final File file;
    private final String fileName;

    private final Properties properties = new Properties();

    private Preferinte(final String fileName) throws IOException {
	this.fileName = fileName;
	this.file = new File(fileName);
	if (this.file.exists()) {
	    FileInputStream fileInputStream = new FileInputStream(this.file);
	    try {
		this.properties.load(fileInputStream);
	    } catch (IOException e) {
		e.printStackTrace();
	    } finally {
		fileInputStream.close();
	    }
	}
    }

    public synchronized void flush() throws IOException {
	File parentFile = this.file.getParentFile();
	if ((parentFile != null) && (!parentFile.exists())) {
	    parentFile.mkdirs();
	}
	FileOutputStream fileOutputStream = new FileOutputStream(this.file);
	try {
	    this.properties.store(fileOutputStream, "");
	} finally {
	    fileOutputStream.close();
	}
    }

    public String get(final String key, final String def) {
	String v = (String) this.properties.get(key);
	if (v == null) {
	    return def;
	}
	return v;
    }

    public String getFileName() {
	return this.fileName;
    }

    public int getInt(final String key, final int i) {
	String v = (String) this.properties.get(key);
	if (v == null) {
	    return i;
	}
	return Integer.parseInt(v);
    }

    public void put(final String key, final String val) {
	this.properties.put(key, val);
    }

    public void putInt(final String key, final int i) {
	this.properties.put(key, String.valueOf(i));
    }

}
