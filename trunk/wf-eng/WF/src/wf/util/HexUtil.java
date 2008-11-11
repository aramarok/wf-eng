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
package wf.util;
import java.io.*;
import java.util.*;


public class HexUtil
{

  /**
   * <p>The nibbles' hexadecimal values. A nibble is a half byte.</p>
   */
  protected static final byte hexval[] =
      {(byte) '0', (byte) '1', (byte) '2', (byte) '3',
       (byte) '4', (byte) '5', (byte) '6', (byte) '7',
       (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
       (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F'};



  /**
   * <p>Converts a string into its hexadecimal notation.</p>
   *
   * <p><strong>FIXME:</strong> If this method is called frequently,
   * it should directly implement the algorithm in the called method
   * in order to avoid creating a string instance.</p>
   */
  public static String hexEncode(final String s)
  {
    return hexEncode(s.getBytes());
  }



  /**
   * <p>Converts a byte array into its hexadecimal notation.</p>
   */
  public static String hexEncode(final byte[] s)
  {
    return hexEncode(s, 0, s.length);
  }



  /**
   * <p>Converts a part of a byte array into its hexadecimal
   * notation.</p>
   */
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



  /**
   * <p>Converts a single byte into its hexadecimal notation.</p>
   */
  public static String hexEncode(final byte b)
  {
    StringBuffer sb = new StringBuffer(2);
    sb.append((char) hexval[(b & 0xF0) >> 4]);
    sb.append((char) hexval[(b & 0x0F) >> 0]);
    return sb.toString();
  }



  /**
   * <p>Converts a short value (16-bit) into its hexadecimal
   * notation.</p>
   */
  public static String hexEncode(final short s)
  {
    StringBuffer sb = new StringBuffer(4);
    sb.append((char) hexval[(s & 0xF000) >> 12]);
    sb.append((char) hexval[(s & 0x0F00) >>  8]);
    sb.append((char) hexval[(s & 0x00F0) >>  4]);
    sb.append((char) hexval[(s & 0x000F) >>  0]);
    return sb.toString();
  }



  /**
   * <p>Converts an int value (32-bit) into its hexadecimal
   * notation.</p>
   */
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



  /**
   * <p>Converts a long value (64-bit) into its hexadecimal
   * notation.</p>
   */
  public static String hexEncode(final long l)
  {
    StringBuffer sb = new StringBuffer(16);
    sb.append(hexEncode((int) (l & 0xFFFFFFFF00000000L) >> 32));
    sb.append(hexEncode((int) (l & 0x00000000FFFFFFFFL) >>  0));
    return sb.toString();
  }


  /**
   * <p>Decodes the hexadecimal representation of a sequence of
   * bytes into a byte array. Each character in the string
   * represents a nibble (half byte) and must be one of the
   * characters '0'-'9', 'A'-'F' or 'a'-'f'.</p>
   *
   * @param s The string to be decoded
   *
   * @return The bytes
   *
   * @throws IllegalArgumentException if the string does not contain
   * a valid representation of a byte sequence.
   */
  public static byte[] hexDecode(final String s)
  {
    final int length = s.length();

    /* The string to be converted must have an even number of
    characters. */
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



  /**
   * <p>Decodes a nibble.</p>
   *
   * @param c A character in the range '0'-'9' or 'A'-'F'. Lower
   * case is not supported here.
   *
   * @return The decoded nibble in the range 0-15
   *
   * @throws IllegalArgumentException if <em>c</em> is not a
   * permitted character
   */
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

  /**
   * <p>For testing.</p>
   */
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

        /*
        String bytes = hexEncode(s);
        System.out.print("Hex encoded (String): ");
        System.out.println(bytes);
        System.out.print("Hex encoded (byte[]): ");
        System.out.println(hexEncode(s.getBytes()));
        System.out.print("Re-decoded (byte[]):  ");
        System.out.println(new String(hexDecode(bytes)));
        */

      }
    }
    while (s != null);
  }

}
