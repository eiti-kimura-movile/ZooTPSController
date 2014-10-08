package zoo.experiment;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class Client {

    public static void main(String[] args) throws IOException, InterruptedException {

        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 10000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println(event.getType().name() + " - " + event);
            }
        });

        String lockName = "access";
        String lockBasePath = "/locks/counter1";
        DistributedLock distributedLock = new DistributedLock(zk, lockBasePath, lockName);

        try {
            // lock it in a distributed fashion
            String lock = distributedLock.lock();
            System.out.println("doing a operation using lock: " + lock);
        } finally {
            distributedLock.unlock();
            zk.close();
        }
    }

}
