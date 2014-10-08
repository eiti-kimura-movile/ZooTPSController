package zoo.experiment;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class DistributedLock {

    private final ZooKeeper zk;
    private final String lockBasePath;
    private final String lockName;

    private String lockPath;

    public DistributedLock(ZooKeeper zk, String lockBasePath, String lockName) {
        this.zk = zk;
        this.lockBasePath = lockBasePath;
        this.lockName = lockName;
    }

    public String lock() throws IOException {
        try {
            // lockPath will be different than (lockBasePath + "/" + lockName) becuase of the sequence number ZooKeeper appends
            lockPath = zk.create(lockBasePath + "/" + lockName + "-", null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            final Object lock = new Object();

            synchronized (lock) {
                while (true) {
                    List<String> nodes = zk.getChildren(lockBasePath, new Watcher() {
                        @Override
                        public void process(WatchedEvent event) {
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        }
                    });

                    Collections.sort(nodes); // ZooKeeper node names can be sorted lexographically

                    if (lockPath.endsWith(nodes.get(0))) {
                        return lockPath;
                    } else {
                        lock.wait();
                    }
                }
            }
        } catch (KeeperException e) {
            throw new IOException(e);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    public void unlock() throws IOException {
        try {
            if (lockPath != null) {
                zk.delete(lockPath, -1);
                lockPath = null;
            }
        } catch (KeeperException e) {
            throw new IOException(e);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }
}