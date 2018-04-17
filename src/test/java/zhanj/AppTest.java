package zhanj;

import org.junit.Test;

import java.io.IOException;

/**
 * Unit test for simple Executor.
 */
public class AppTest {

    @Test
    public void testElection() {
        String znode = "/zhanj";
        try {
            TestThread t1 = new TestThread("172.27.24.146:2181", znode, "app1");
            TestThread t2 = new TestThread("172.27.24.146:2181", znode, "app2");
            TestThread t3 = new TestThread("172.27.24.146:2181", znode, "app3");
            t1.start();
            t2.start();
            t3.start();
            Thread.sleep(30000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
