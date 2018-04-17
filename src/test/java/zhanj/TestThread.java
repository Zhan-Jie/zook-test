package zhanj;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class TestThread extends Thread {
    private App app;

    public TestThread(String hostPort, String znode, String myname) throws IOException {
        this.app = new App(hostPort, znode, myname);
    }

    @Override
    public void run() {
        try {
            this.app.raiseAnElection();
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
