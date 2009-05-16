package wf.client.manager;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.commons.beanutils.BeanUtils;

public class Utilities {

    static JFileChooser fileChooser;

    @SuppressWarnings("unchecked")
    public static DefaultListModel createListModel(final List v) {
	DefaultListModel lm = new DefaultListModel();
	for (Iterator j = v.iterator(); j.hasNext();) {
	    Object o = j.next();
	    lm.addElement(o);
	}
	return lm;
    }

    @SuppressWarnings( { "unchecked", "serial" })
    public static TableModel createTableModel(final List v)
	    throws IllegalAccessException, NoSuchMethodException,
	    InvocationTargetException {
	if ((v == null) || (v.size() == 0)) {
	    return new DefaultTableModel(new Object[0][0],
		    new String[] { "none" });
	}
	Object o = v.get(0);
	String[] propertiesNames = getNamesOfObjectProperties(o);
	Object[][] data = createTableModelData(v, propertiesNames);
	DefaultTableModel tm = new DefaultTableModel(data, propertiesNames) {
	    @Override
	    public boolean isCellEditable(int row, int column) {
		return false;
	    }
	};
	return tm;
    }

    @SuppressWarnings("unchecked")
    private static Object[][] createTableModelData(final List v,
	    final String[] propertiesNames) throws IllegalAccessException,
	    NoSuchMethodException, InvocationTargetException {

	Object[][] objects = new Object[v.size()][];
	for (int i = 0; i < objects.length; i++) {
	    objects[i] = getProperties(v.get(i), propertiesNames);

	}
	return objects;
    }

    public static Object getColumnValue(final JTable t, final String colName,
	    final int row) {
	TableModel model = t.getModel();
	for (int i = 0; i < model.getColumnCount(); i++) {
	    if (colName.equals(model.getColumnName(i))) {
		return model.getValueAt(row, i);
	    }
	}
	return null;
    }

    public static JFileChooser getFileChooser() {
	if (fileChooser == null) {
	    fileChooser = new JFileChooser(".");
	    fileChooser.setMultiSelectionEnabled(false);
	}
	return fileChooser;
    }

    @SuppressWarnings("unchecked")
    public static String[] getNamesOfObjectProperties(final Object o)
	    throws NoSuchMethodException, IllegalAccessException,
	    InvocationTargetException {
	Map m = BeanUtils.describe(o);
	Set keys = m.keySet();
	ArrayList lk = new ArrayList(keys);
	Collections.sort(lk);
	return (String[]) lk.toArray(new String[0]);
    }

    private static String[] getProperties(final Object o,
	    final String[] propertiesNames) throws NoSuchMethodException,
	    IllegalAccessException, InvocationTargetException {
	String[] values = new String[propertiesNames.length];
	for (int i = 0; i < propertiesNames.length; i++) {
	    String propertiesName = propertiesNames[i];
	    values[i] = BeanUtils.getProperty(o, propertiesName);
	}
	return values;
    }

}
