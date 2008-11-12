package kgi;


public class Test {

  public static class T2 extends Thread{
    public void run() {
      for( int i = 0; i < 200; i++){
        System.out.println("i=" + i);
      }
    }

  }

  public class T3 extends Thread{
    public void run() {
      for( int i = 0; i < 200; i++){
        System.out.println("i=" + i);
      }
    }

  }


  public static void main(String[] args) {
    T2 t2 = new T2();
    t2.start();

    
  }

}


