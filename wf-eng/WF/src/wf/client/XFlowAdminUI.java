/*
* XFlowManager.java
*
* Created on May 11, 2004, 10:31 PM
*/

package wf.client;

import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;

import wf.client.auth.User;
import wf.client.manager.DesktopPane;
import wf.client.manager.WinListener;
import wf.exceptions.XflowException;
import wf.model.WorkflowState;


/**
 *
 * @author  kosta
 */
public class XFlowAdminUI {

  public static final String USERNAME = "USER";
  public static final String PASSWORD = "PASSWORD";

  DesktopPane desk = null;
  Preferences preferences;

  private static XFlowAdminUI _instance;


  public static XFlowAdminUI getInstance(){
    return _instance;
  }

  /** Creates a new instance of XFlowManager */
  private XFlowAdminUI( Preferences pref) throws IOException {
    _instance = this;
    preferences = pref;
    desk = new DesktopPane( this );
    desk.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    desk.setSize( preferences.getInt( "MAIN_SCREEN_W", 800), preferences.getInt( "MAIN_SCREEN_H", 600) );    
    desk.setTitle( "XFlow Manager UI");
    desk.addWindowListener( new WinListener( preferences, "MAIN_SCREEN" ) );
    desk.setVisible( true );

    preferences.flush();

  }


  public User getUSER() {
    //if( _USER == null ){
    return   new User( preferences.get( USERNAME,  "kgi"), preferences.get( PASSWORD, "password") );
    //}
    // return _USER;
  }

  public void setUSER(User user) {
    preferences.put( USERNAME,  user.getName());
    preferences.put( PASSWORD, user.getPassword() );
    //this._USER = user;
  }

  public List getKnownWorkflows() throws XflowException {
    List v = WorkflowManager.getWorkflowModels( getUSER() );
    return v;
  }


  public Preferences getPreferences() {
    return preferences;
  }

  public void setPreferences(Preferences preferences) {
    this.preferences = preferences;
  }

  public List getActiveWorkflows() throws XflowException {
    List v = WorkflowManager.getActiveWorkflows( getUSER() );
    return v;
  }

  public WorkflowState getWorkflowState ( int wfid) throws XflowException {
    WorkflowState  state =  WorkflowManager.getWorkflowState( new Integer( wfid ) , getUSER() );
    return state;
  }

  public static void main( String[] args ){
    final XFlowAdminUI xFlowAdminUI;
    try {
      xFlowAdminUI = new XFlowAdminUI( Preferences.getInstance( "XFlowManager.properties" ) );

      Runtime.getRuntime().addShutdownHook( new Thread(){
        public void run() {
          try {
            System.out.println("Flushing preferences.");
            xFlowAdminUI.getPreferences().flush();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });

    } catch (IOException e) {
      e.printStackTrace();
      System.exit( -1 );
    }
  }
}
