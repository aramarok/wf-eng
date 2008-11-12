
package wf.jms;


public class JMSShutdownHook extends Thread {

   public JMSShutdownHook () {
   }

   public void run () {
       try {
           JMSTopicConnection.close();
           SynchQueueMessaging.close();
       } catch (Exception e) {
           System.out.println (e.getMessage());
       }
   }
}
