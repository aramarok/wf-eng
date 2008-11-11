package xflow.client.manager;

import org.apache.commons.beanutils.BeanUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by
 * User: kosta
 * Date: May 11, 2004
 * Time: 10:58:46 PM
 */
public class Utilities {

  static JFileChooser fileChooser;

  public static JFileChooser getFileChooser(){
    if( fileChooser == null ){
      fileChooser = new JFileChooser(".");
      fileChooser.setMultiSelectionEnabled( false );
    }
    return fileChooser;
  }


  public static DefaultListModel createListModel( List v ){
    DefaultListModel lm = new DefaultListModel();
    for (Iterator j = v.iterator(); j.hasNext();) {
      Object o = (Object) j.next();
     lm.addElement( o );
    }
    return lm;
  }

  public static TableModel createTableModel( List v ) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    if( v == null || v.size() == 0 ){
      return new DefaultTableModel( new Object[0][0], new String[]{ "none"} );
    }
    Object o = v.get( 0 );
    String[] propertiesNames = getNamesOfObjectProperties( o );
    Object[][] data = createTableModelData( v, propertiesNames );
    DefaultTableModel tm = new DefaultTableModel( data, propertiesNames ){
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    return tm;
  }

  private static Object[][] createTableModelData(List v, String[] propertiesNames) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

    Object[][] objects = new Object[ v.size() ][ ];
    for (int i = 0; i < objects.length; i++) {
      objects[i] = getProperties( v.get( i ), propertiesNames );

    }
    return objects;
  }

  private static String[] getProperties(Object o, String[] propertiesNames) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    String[] values = new String[ propertiesNames.length];
    for (int i = 0; i < propertiesNames.length; i++) {
      String propertiesName = propertiesNames[i];
      values[i] = BeanUtils.getProperty( o, propertiesName );
    }
    return values;
  }

  public static String[] getNamesOfObjectProperties( Object o ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    Map m = BeanUtils.describe( o );
    Set keys = m.keySet();
    ArrayList lk = new ArrayList( keys );
    Collections.sort( lk );
    return (String[]) lk.toArray( new String[0]);
  }

  public static Object getColumnValue( JTable t, String colName, int row ){
    TableModel model = t.getModel();
    for( int i = 0; i < model.getColumnCount(); i++ ){
      if( colName.equals( model.getColumnName( i ) ) ){
        return model.getValueAt( row, i );
      }
    }
    return null;
  }

}
