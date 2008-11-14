

package wf.client.manager;

import wf.client.WFClientConstants;
import wf.client.Admin;
import wf.client.manager.ActiveProcessesWin;
import wf.client.manager.ActiveWorkflowWin;


public class DesktopPane extends javax.swing.JFrame {
    
    WorkflowTypesWin workflowTypesWin = null;
    ActiveWorkflowWin activeWorkflowWin = null;
    ActiveProcessesWin activeProcessesWin = null;
    SettingsDlg settingsDlg = null;


    Admin xFlowAdminUI;
    
    public DesktopPane() {
        initComponents();
    }

  public DesktopPane(Admin xFlowAdminUI)  {
    this();
    this.xFlowAdminUI = xFlowAdminUI;
  }

    
    private void initComponents() {
        jDesktopPane = new javax.swing.JDesktopPane();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        miSettings = new javax.swing.JMenuItem();

        getContentPane().add(jDesktopPane, java.awt.BorderLayout.CENTER);

        jMenu2.setMnemonic('w');
        jMenu2.setText("Show window");
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource(WFClientConstants.CLIENT_ICON_LOCATION + "types.gif")));
        jMenuItem1.setMnemonic('t');
        jMenuItem1.setText("Workflow Types");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showWorkflowTypesWindow(evt);
            }
        });

        jMenu2.add(jMenuItem1);

        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource(WFClientConstants.CLIENT_ICON_LOCATION + "execute.gif")));
        jMenuItem2.setMnemonic('c');
        jMenuItem2.setText("Active Workflow");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showActiveWfWin(evt);
            }
        });

        jMenu2.add(jMenuItem2);

        jMenu2.add(jSeparator1);

        miSettings.setMnemonic('e');
        miSettings.setText("Settings");
        miSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miSettingsActionPerformed(evt);
            }
        });

        jMenu2.add(miSettings);

        jMenuBar2.add(jMenu2);

        setJMenuBar(jMenuBar2);

    }

    private void miSettingsActionPerformed(java.awt.event.ActionEvent evt) {
       showSettingsDialog();
    }

  private void showSettingsDialog() {
    if( settingsDlg == null ){
      settingsDlg = new SettingsDlg( this, true );
    }
    settingsDlg.showModalDialog( Admin.getInstance().getPreferences() );
  }

  private void showActiveWfWin(java.awt.event.ActionEvent evt) {
        if( activeWorkflowWin == null ){
          activeWorkflowWin = new ActiveWorkflowWin( xFlowAdminUI, this );
          activeWorkflowWin.setSize( 300, 200 );
          activeWorkflowWin.addInternalFrameListener( new WinListener( "activeWorkflowWin"));
           jDesktopPane.add( activeWorkflowWin );
        }
      activeWorkflowWin.setVisible( true );
    }

    private void showWorkflowTypesWindow(java.awt.event.ActionEvent evt) {
       if( workflowTypesWin == null ){
           workflowTypesWin = new WorkflowTypesWin( xFlowAdminUI );
           workflowTypesWin.setSize( 300, 200 );
           workflowTypesWin.addInternalFrameListener( new WinListener( "workflowTypesWin"));
           jDesktopPane.add( workflowTypesWin );
       }
       workflowTypesWin.setVisible( true );
    }
    private javax.swing.JDesktopPane jDesktopPane;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JMenuItem miSettings;

   public void showActiveProcessesWin(int selectedWfID) {
      if( activeProcessesWin == null  ){
        activeProcessesWin = new ActiveProcessesWin( xFlowAdminUI );
          activeProcessesWin.setSize( 300, 200 );
          activeWorkflowWin.addInternalFrameListener( new WinListener( "activeProcessesWin"));
           jDesktopPane.add( activeProcessesWin );
       }
       activeProcessesWin.setVisible( true );
       activeProcessesWin.showProcessesFoWfID( selectedWfID );
   }

}
