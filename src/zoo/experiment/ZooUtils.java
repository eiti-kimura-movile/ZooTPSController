package zoo.experiment;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

public class ZooUtils {

    public static ZooKeeper connect(String hosts, int timeout) throws IOException, InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        ZooKeeper zk = new ZooKeeper(hosts, timeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    signal.countDown();
                }
            }
        });
        signal.await();
        return zk;
    }

    public void create(ZooKeeper zk, String groupName) throws KeeperException, InterruptedException {
        String path = "/" + groupName;
        String createdPath = zk.create(path, null /* data */, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("Created " + createdPath);
    }
}
