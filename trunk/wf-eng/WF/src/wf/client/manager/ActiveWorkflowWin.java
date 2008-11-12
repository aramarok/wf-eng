

package wf.client.manager;

import wf.client.WFClientConstants;
import wf.client.XFlowAdminUI;
import wf.exceptions.XflowException;

import java.lang.reflect.InvocationTargetException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;


import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;


public class ActiveWorkflowWin extends javax.swing.JInternalFrame {

  XFlowAdminUI xFlowAdminUI;
  DesktopPane desktopPane;

  
  public ActiveWorkflowWin() {
    initComponents();
    activeWfTable.addMouseListener( new MouseAdapter(){
      public void mouseClicked(MouseEvent e) {
        if( e.getClickCount() > 1 ){
          desktopPane.showActiveProcessesWin(  getSelectedWfID() );
        }
      }
    });

    final JPopupMenu popMenu = new JPopupMenu("Actions");
    JMenuItem mi = new JMenuItem( "show active processes" );
    mi.addActionListener( new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        desktopPane.showActiveProcessesWin(  getSelectedWfID() );
      }
    });
    popMenu.add( mi );

    MouseAdapter popupAdapter = new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
      }

      public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
      }

      private void maybeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
          popMenu.show(e.getComponent(),
              e.getX(), e.getY());
        }
      }
    };

    activeWfTable.addMouseListener( popupAdapter );
  }

  private int getSelectedWfID() {
    int row = activeWfTable.getSelectedRow();
    String id = (String) Utilities.getColumnValue( activeWfTable, "workflowId", row );
    return Integer.parseInt( id );
  }

  public ActiveWorkflowWin(XFlowAdminUI xFlowAdminUI, DesktopPane desk) {
    this();
    this.xFlowAdminUI = xFlowAdminUI;
    this.desktopPane = desk;
  }

  
    private void initComponents() {
        jToolBar1 = new javax.swing.JToolBar();
        refreshBtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        activeWfTable = new javax.swing.JTable();

        setClosable(true);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Active Workflows");
        jToolBar1.setFloatable(false);
        refreshBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource(WFClientConstants.CLIENT_ICON_LOCATION + "refresh.gif")));
        refreshBtn.setMnemonic('r');
        refreshBtn.setText("refresh");
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });

        jToolBar1.add(refreshBtn);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

        activeWfTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(activeWfTable);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        pack();
    }

  private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {
    try {
      activeWfTable.setModel( Utilities.createTableModel( xFlowAdminUI.getActiveWorkflows() ));
    } catch ( Exception e) {
      e.printStackTrace();
    }
  }
    private javax.swing.JTable activeWfTable;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton refreshBtn;

}
