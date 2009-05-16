package wf.client.manager;

import wf.client.Admin;
import wf.client.Preferinte;

public class SettingsDlg extends javax.swing.JDialog {

    public static final String KEY = "SettingsDlg";

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("deprecation")
    public static void main(final String args[]) {
	new SettingsDlg(new javax.swing.JFrame(), true).show();
    }

    private javax.swing.JButton jbCancel;

    private javax.swing.JButton jbOK;

    private javax.swing.JLabel jLabel1;

    private javax.swing.JLabel jLabel2;

    private javax.swing.JPanel jPanel1;

    private javax.swing.JPanel jPanel2;

    private javax.swing.JPanel jPanel3;

    Preferinte preferences;

    private boolean result = false;

    private javax.swing.JTextField tfPassword;
    private javax.swing.JTextField tfUserName;

    public SettingsDlg(final java.awt.Frame parent, final boolean modal) {
	super(parent, modal);
	this.initComponents();
	this.addWindowListener(new WinListener(KEY));
    }

    private void displayPreferences() {
	this.tfUserName.setText(this.preferences.get(Admin.USERNAME, "None"));
	this.tfPassword.setText(this.preferences.get(Admin.PASSWORD, ""));
    }

    private void initComponents() {
	java.awt.GridBagConstraints gridBagConstraints;

	this.jPanel1 = new javax.swing.JPanel();
	this.jLabel1 = new javax.swing.JLabel();
	this.tfUserName = new javax.swing.JTextField();
	this.jLabel2 = new javax.swing.JLabel();
	this.tfPassword = new javax.swing.JTextField();
	this.jPanel3 = new javax.swing.JPanel();
	this.jPanel2 = new javax.swing.JPanel();
	this.jbOK = new javax.swing.JButton();
	this.jbCancel = new javax.swing.JButton();

	this
		.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
	this.jPanel1.setLayout(new java.awt.GridBagLayout());

	this.jLabel1.setText("Utilizator name:");
	gridBagConstraints = new java.awt.GridBagConstraints();
	gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
	this.jPanel1.add(this.jLabel1, gridBagConstraints);

	gridBagConstraints = new java.awt.GridBagConstraints();
	gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
	gridBagConstraints.weightx = 10.0;
	this.jPanel1.add(this.tfUserName, gridBagConstraints);

	this.jLabel2.setText("Password:");
	this.jLabel2.setToolTipText("Password:");
	gridBagConstraints = new java.awt.GridBagConstraints();
	gridBagConstraints.gridx = 0;
	gridBagConstraints.gridy = 1;
	gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
	this.jPanel1.add(this.jLabel2, gridBagConstraints);

	gridBagConstraints = new java.awt.GridBagConstraints();
	gridBagConstraints.gridx = 1;
	gridBagConstraints.gridy = 1;
	gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
	this.jPanel1.add(this.tfPassword, gridBagConstraints);

	gridBagConstraints = new java.awt.GridBagConstraints();
	gridBagConstraints.gridx = 1;
	gridBagConstraints.gridy = 2;
	gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
	gridBagConstraints.weightx = 10.0;
	gridBagConstraints.weighty = 10.0;
	this.jPanel1.add(this.jPanel3, gridBagConstraints);

	this.getContentPane().add(this.jPanel1, java.awt.BorderLayout.CENTER);

	this.jbOK.setMnemonic('O');
	this.jbOK.setText("OK");
	this.jbOK.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(final java.awt.event.ActionEvent evt) {
		SettingsDlg.this.jbOKActionPerformed(evt);
	    }
	});

	this.jPanel2.add(this.jbOK);

	this.jbCancel.setMnemonic('c');
	this.jbCancel.setText("Cancel");
	this.jbCancel.addActionListener(new java.awt.event.ActionListener() {
	    public void actionPerformed(final java.awt.event.ActionEvent evt) {
		SettingsDlg.this.jbCancelActionPerformed(evt);
	    }
	});

	this.jPanel2.add(this.jbCancel);

	this.getContentPane().add(this.jPanel2, java.awt.BorderLayout.SOUTH);

	this.pack();
    }

    private void jbCancelActionPerformed(final java.awt.event.ActionEvent evt) {
	this.result = false;
	this.setVisible(false);
    }

    private void jbOKActionPerformed(final java.awt.event.ActionEvent evt) {
	this.storePreferences();
	this.result = true;
	this.setVisible(false);
    }

    @SuppressWarnings("deprecation")
    public boolean showModalDialog(final Preferinte pref) {
	this.preferences = pref;
	this.displayPreferences();
	this.setModal(true);
	this.show();
	if (this.result) {
	    this.storePreferences();
	}
	return this.result;
    }

    private void storePreferences() {
	this.preferences.put(Admin.USERNAME, this.tfUserName.getText());
	this.preferences.put(Admin.PASSWORD, this.tfPassword.getText());
	try {
	    this.preferences.flush();
	} catch (Exception e1) {
	    e1.printStackTrace();
	}
    }

}
