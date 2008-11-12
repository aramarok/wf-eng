package wf.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

/**
 * Created because jdk's 1.4.2_04 preferences package does not work properly on Linux
 * User: kosta
 * Date: May 24, 2004
 * Time: 8:00:32 PM
 */
public class Preferences {

  private static Map preferences = new Hashtable();

  private String fileName;
  private File file;
  private Properties properties = new Properties();

  public String getFileName() {
    return fileName;
  }

  private Preferences( String fileName ) throws IOException {
    this.fileName = fileName;
    file = new File( fileName );
    if( file.exists() ){
      FileInputStream fileInputStream = new FileInputStream( file );
      try {
        properties.load( fileInputStream);
      } catch (IOException e) {
        e.printStackTrace();
      } finally{
        fileInputStream.close();
      }
    }
  }

  public synchronized void flush() throws IOException {
    File parentFile = file.getParentFile();
    if( parentFile!= null &&  (! parentFile.exists() ) ){
      parentFile.mkdirs();
    }
    FileOutputStream fileOutputStream = new FileOutputStream( file );
    try {
      properties.store( fileOutputStream, "" );
    } finally {
      fileOutputStream.close();
    }
  }

  public static synchronized Preferences getInstance( String fileName ) throws IOException {
    Preferences p = (Preferences) preferences.get( fileName );
    if( p == null ){
      p = new Preferences( fileName );
      preferences.put( fileName, p );
    }
    return p;

  }

  public String  get(String key,String def) {
    String v = (String) properties.get( key );
    if( v == null ) return def;
    return v;
  }

  public int getInt(String key, int i) {
    String v = (String) properties.get( key );
    if( v == null ) return i;
    return Integer.parseInt( v );
  }

  public void put( String key, String val ){
    properties.put( key, val );
  }

  public void putInt(String key, int i) {
    properties.put( key, String.valueOf( i ) );
  }

}
