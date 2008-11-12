
package wf.util;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class HexUtil
{

  
  protected static final byte hexval[] =
      {(byte) '0', (byte) '1', (byte) '2', (byte) '3',
       (byte) '4', (byte) '5', (byte) '6', (byte) '7',
       (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
       (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F'};



  
  public static String hexEncode(final String s)
  {
    return hexEncode(s.getBytes());
  }



  
  public static String hexEncode(final byte[] s)
  {
    return hexEncode(s, 0, s.length);
  }



  
  public static String hexEncode(final byte[] s, final int offset,
                                 final int length)
  {
    StringBuffer b = new StringBuffer(length * 2);
    for (int i = offset; i < offset + length; i++)
    {
      int c = s[i];
      b.append((char) hexval[(c & 0xF0) >> 4]);
      b.append((char) hexval[(c & 0x0F) >> 0]);
    }
    return b.toString();
  }



  
  public static String hexEncode(final byte b)
  {
    StringBuffer sb = new StringBuffer(2);
    sb.append((char) hexval[(b & 0xF0) >> 4]);
    sb.append((char) hexval[(b & 0x0F) >> 0]);
    return sb.toString();
  }



  
  public static String hexEncode(final short s)
  {
    StringBuffer sb = new StringBuffer(4);
    sb.append((char) hexval[(s & 0xF000) >> 12]);
    sb.append((char) hexval[(s & 0x0F00) >>  8]);
    sb.append((char) hexval[(s & 0x00F0) >>  4]);
    sb.append((char) hexval[(s & 0x000F) >>  0]);
    return sb.toString();
  }



  
  public static String hexEncode(final int i)
  {
    StringBuffer sb = new StringBuffer(8);
    sb.append((char) hexval[(i & 0xF0000000) >> 28]);
    sb.append((char) hexval[(i & 0x0F000000) >> 24]);
    sb.append((char) hexval[(i & 0x00F00000) >> 20]);
    sb.append((char) hexval[(i & 0x000F0000) >> 16]);
    sb.append((char) hexval[(i & 0x0000F000) >> 12]);
    sb.append((char) hexval[(i & 0x00000F00) >>  8]);
    sb.append((char) hexval[(i & 0x000000F0) >>  4]);
    sb.append((char) hexval[(i & 0x0000000F) >>  0]);
    return sb.toString();
  }



  
  public static String hexEncode(final long l)
  {
    StringBuffer sb = new StringBuffer(16);
    sb.append(hexEncode((int) (l & 0xFFFFFFFF00000000L) >> 32));
    sb.append(hexEncode((int) (l & 0x00000000FFFFFFFFL) >>  0));
    return sb.toString();
  }


  
  public static byte[] hexDecode(final String s)
  {
    final int length = s.length();

    
    if (length % 2 == 1)
      throw new IllegalArgumentException
          ("String has odd length " + length);
    byte[] b = new byte[length / 2];
    char[] c = new char[length];
    s.toUpperCase().getChars(0, length, c, 0);
    for (int i = 0; i < length; i += 2)
      b[i/2] = (byte) (decodeNibble(c[i]) << 4 & 0xF0 |
          decodeNibble(c[i+1])    & 0x0F);
    return b;
  }



  
  protected static byte decodeNibble(final char c)
  {
    for (byte i = 0; i < hexval.length; i++)
      if ((byte) c == hexval[i])
        return i;
    throw new IllegalArgumentException("\"" + c + "\"" +
        " does not represent a nibble.");
  }

  public static String hexEncodeObject (Object obj) {

    String valueStr = null;

    try {
      byte[] barr = null;
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectOutputStream s = new ObjectOutputStream(out);
      s.writeObject (obj);
      s.flush();
      barr = out.toByteArray();
      valueStr = hexEncode (barr);
    } catch (IOException ie) {
      throw new RuntimeException( ie );
    }
    return valueStr;
  }

  public static Object hexDecodeObject (final String bytes) {

    byte[] barr = hexDecode (bytes);
    Object obj = null;
    try {
      ByteArrayInputStream in = new ByteArrayInputStream(barr);
      ObjectInputStream sin = new ObjectInputStream(in);
      obj = sin.readObject();
    } catch (Exception ie) {
      System.out.println (ie.getMessage());
    }
    return obj;
  }

  
  public static void main(final String args[])
      throws IOException
  {
    final BufferedReader in =
        new BufferedReader(new InputStreamReader(System.in));
    String s;
    do
    {
      s = in.readLine();
      if (s != null)
      {
        String bytes = hexEncodeObject(s);
        System.out.print("Hex encoded (object): ");
        System.out.println(bytes);
        System.out.print("Re-decoded (object):  ");
        System.out.println(hexDecodeObject(bytes));

        

      }
    }
    while (s != null);
  }

}
