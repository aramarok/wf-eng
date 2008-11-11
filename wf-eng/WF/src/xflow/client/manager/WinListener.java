package xflow.client.manager;

import xflow.client.XFlowAdminUI;
import xflow.client.Preferences;
import xflow.client.Preferences;

import javax.swing.event.InternalFrameListener;
import javax.swing.event.InternalFrameEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.*;

/**
 * Created by
 * User: kosta
 * Date: May 23, 2004
 * Time: 11:35:42 PM
 */
public class WinListener implements WindowListener, WindowStateListener,InternalFrameListener{

  Preferences preferences;
  String windowKey;

  public WinListener( String windowKey) {
    this(XFlowAdminUI.getInstance().getPreferences(), windowKey);
  }

  public WinListener(Preferences preferences, String windowKey) {
    this.preferences = preferences;
    this.windowKey = windowKey;
  }

  public static void restoreComponentState( Component w, Preferences preferences, String windowKey ){
    w.setSize( preferences.getInt( windowKey +"_W", w.getWidth() ),
        preferences.getInt(  windowKey +"_H", w.getHeight() ) );
    w.setLocation(   preferences.getInt( windowKey +"_X", w.getX() ),
        preferences.getInt(  windowKey +"_Y", w.getY() ) );
  }

  public void windowActivated(WindowEvent e) {

  }

  public void windowClosed(WindowEvent e) {

  }

  public void windowClosing(WindowEvent e) {
    storePositionAndSize(e.getWindow());

  }

  private void storePositionAndSize(Component w ) {
    preferences.putInt( windowKey +"_W", w.getWidth() );
    preferences.putInt(  windowKey +"_H", w.getHeight() );
    preferences.putInt( windowKey +"_X", w.getX() );
    preferences.putInt(  windowKey +"_Y", w.getY() );
    try {
      preferences.flush();
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }

  public void windowDeactivated(WindowEvent e) {
    storePositionAndSize( e.getWindow() );
  }

  public void windowDeiconified(WindowEvent e) {

  }

  public void windowIconified(WindowEvent e) {

  }

  public void windowOpened(WindowEvent e) {
    restoreComponentState( e.getWindow(), preferences, windowKey );
  }

  public void windowStateChanged(WindowEvent e) {

  }


  public void internalFrameActivated(InternalFrameEvent e) {
    restoreComponentState( e.getInternalFrame(), preferences, windowKey );
  }

  public void internalFrameClosed(InternalFrameEvent e) {

  }

  public void internalFrameClosing(InternalFrameEvent e) {
    storePositionAndSize( e.getInternalFrame() );
  }

  public void internalFrameDeactivated(InternalFrameEvent e) {
    storePositionAndSize( e.getInternalFrame() );
  }

  public void internalFrameDeiconified(InternalFrameEvent e) {

  }

  public void internalFrameIconified(InternalFrameEvent e) {

  }

  public void internalFrameOpened(InternalFrameEvent e) {

  }
}
