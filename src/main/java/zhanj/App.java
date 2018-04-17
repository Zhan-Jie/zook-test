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

    public App(String hostPort, String znode, String myname) throws IOException{
        this.znode = znode;
        this.myname = myname;
        zk = new ZooKeeper(hostPort, 3000, this);
    }

    public void raiseAnElection () throws KeeperException, InterruptedException {
        try {
            logger.info("{} raises an election.", myname);
            zk.create(znode, myname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            logger.info("{} becomes the leader~", myname);
        } catch (KeeperException.NodeExistsException e) {
            String leader = new String(zk.getData(znode, false, null));
            logger.info("{} failed the election. {} is the leader.\n continue to watch leader.", myname, leader);
            zk.exists(znode, true);
        }
    }

    @Override
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDeleted) {
            try {
                logger.info("Leader is down.");
                raiseAnElection();
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
