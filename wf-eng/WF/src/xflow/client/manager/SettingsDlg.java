/*
* SettingsDlg.java
*
* Created on May 23, 2004, 11:18 PM
*/

package xflow.client.manager;

import xflow.client.XFlowAdminUI;
import xflow.client.Preferences;

import java.awt.*;

import xflow.client.XFlowAdminUI;
import xflow.client.Preferences;

/**
 *
 * @author  kosta
 */
public class SettingsDlg extends javax.swing.JDialog {

  private boolean result = false;

  Preferences preferences;

  public static final String KEY = "SettingsDlg";


  /** Creates new form SettingsDlg */
  public SettingsDlg(java.awt.Frame parent, boolean modal) {
    super(parent, modal);
    initComponents();
    addWindowListener( new WinListener( KEY ));
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tfUserName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tfPassword = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jbOK = new javax.swing.JButton();
        jbCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("User name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 10.0;
        jPanel1.add(tfUserName, gridBagConstraints);

        jLabel2.setText("Password:");
        jLabel2.setToolTipText("Password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(tfPassword, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 10.0;
        gridBagConstraints.weighty = 10.0;
        jPanel1.add(jPanel3, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jbOK.setMnemonic('O');
        jbOK.setText("OK");
        jbOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbOKActionPerformed(evt);
            }
        });

        jPanel2.add(jbOK);

        jbCancel.setMnemonic('c');
        jbCancel.setText("Cancel");
        jbCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbCancelActionPerformed(evt);
            }
        });

        jPanel2.add(jbCancel);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }//GEN-END:initComponents

  private void jbCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbCancelActionPerformed
    result = false;
    setVisible( false );
  }//GEN-LAST:event_jbCancelActionPerformed

  private void jbOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbOKActionPerformed
    storePreferences();
    result = true;
    setVisible( false );
  }//GEN-LAST:event_jbOKActionPerformed

  /**
   * @param args the command line arguments
   */
  public static void main(String args[]) {
    new SettingsDlg(new javax.swing.JFrame(), true).show();
  }


  public boolean showModalDialog( Preferences pref ){
    preferences = pref;
    displayPreferences();
    setModal( true );
    show();
    if( result ){
      storePreferences();
    }
    return result;
  }

  private void storePreferences() {
    preferences.put( XFlowAdminUI.USERNAME, tfUserName.getText() );
    preferences.put( XFlowAdminUI.PASSWORD, tfPassword.getText() );
    try {
      preferences.flush();
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }

  private void displayPreferences() {
    tfUserName.setText(  preferences.get( XFlowAdminUI.USERNAME,"None" ) );
    tfPassword.setText(  preferences.get( XFlowAdminUI.PASSWORD,"" ) );
  }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton jbCancel;
    private javax.swing.JButton jbOK;
    private javax.swing.JTextField tfPassword;
    private javax.swing.JTextField tfUserName;
    // End of variables declaration//GEN-END:variables

}
