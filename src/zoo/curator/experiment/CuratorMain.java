package zoo.curator.experiment;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;

public class CuratorMain {

    public static void main(String[] args) throws Exception {

        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        
        CuratorFramework client = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();

        InterProcessSemaphoreV2 semaphore = new InterProcessSemaphoreV2(client, "/lock/op1", 3);
        
        for (int i = 0; i < threads; i++) {
            executor.submit(new ProcessorThread(semaphore, latch));
            Thread.sleep(300);
        }
        
        executor.shutdown();
        latch.await(); // waits for threads to finish processing
        CloseableUtils.closeQuietly(client);
        
        System.out.println("finished!");
    }

}
