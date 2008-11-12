package wf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * User: kosta
 * Date: Jun 28, 2004
 * Time: 12:44:59 AM
 */
public class TestUtilities {

  public static String readFileContent( String fileName ) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(fileName));
    StringBuffer content = new StringBuffer();
    String str;
    while ((str = in.readLine()) != null) {
      content.append( str );
    }
    in.close();
    return content.toString();
  }
}
