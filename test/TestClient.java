import echoclient.EchoClient;
import echoclient.EchoListener;
import echoserver.EchoServer;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Lars Mortensen
 */
public class TestClient {
  
  public TestClient() {
  }
  
  @BeforeClass
  public static void setUpClass() {
    new Thread(new Runnable(){
      @Override
      public void run() {
        EchoServer.main(null);
      }
    }).start();
  }
  
  @AfterClass
  public static void tearDownClass() {
    EchoServer.stopServer();
  }
  
  @Before
  public void setUp() {
  }
  private String testResult = "";
  private CountDownLatch lock;
  @Test
  public void send() throws IOException, InterruptedException{
      lock = new CountDownLatch(1);
    EchoClient client = new EchoClient();
    client.connect("localhost",9090);
    
    
    client.registerEchoListener(new EchoListener() {

        @Override
        public void messageArrived(String data) {
            testResult = data;
            lock.countDown();
            //To change body of generated methods, choose Tools | Templates.
        }
    });
    client.send("Hello");
    lock.await(1000, TimeUnit.MILLISECONDS);
    client.stopServer();
    //assertEquals("HELLO", client.receive());
  }
  
}
