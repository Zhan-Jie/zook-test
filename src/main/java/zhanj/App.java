package zhanj;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class App implements Watcher{
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    private ZooKeeper zk;
    private String znode;
    private String myname;

    public App(String hostPort, String znode, String myname) throws IOException, KeeperException, InterruptedException {
        this.znode = znode;
        this.myname = myname;
        zk = new ZooKeeper(hostPort, 3000, this);
        try {
            logger.info("{} Raises An Election.", myname);
            zk.create(znode, myname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            logger.info("{} becomes the leader~", myname);
        } catch (KeeperException.NodeExistsException e) {
            String leader = new String(zk.getData(znode, false, null));
            logger.info("{} failed the election. {} is the leader. \n continue to watch.", myname, leader);
            zk.exists(znode, true);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDeleted) {
            try {
                logger.info("master is down, {} raises an election.", myname);
                zk.create(znode, myname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                logger.info("{} becomes the leader~", myname);
            } catch (KeeperException.NodeExistsException e) {
                try {
                    String leader = new String(zk.getData(znode, false, null));
                    logger.info("{} failed the election. {} is the leader.\n continue to watch master.", myname, leader);
                    zk.exists(znode, true);
                } catch (KeeperException | InterruptedException e1) {
                    e1.printStackTrace();
                }
            } catch (InterruptedException | KeeperException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        if (args.length != 1) {
            System.err.println("usage: java -jar zook-test.jar <processName>");
            return;
        }
        new App("172.27.24.146:2181", "/zhanj", args[0]);
        System.in.read();
    }
}
