package zoo.curator.experiment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.recipes.locks.Lease;

public class ProcessorThread implements Runnable {

    private InterProcessSemaphoreV2 semaphore;
    private CountDownLatch latch;
    private SimpleDateFormat sdf;
    
    private long startTime;
    private long work = 0; // timeout to simulate work
    
    
    private Random rand = new Random();
    private int low = 10;
    private int high = 600;
    
    
    public ProcessorThread(InterProcessSemaphoreV2 semaphore, CountDownLatch latch) {
        this.semaphore = semaphore;
        this.latch = latch;
        this.sdf = new SimpleDateFormat("HH:mm:ss");
        
        // random value to simulate a worktime
        work = rand.nextInt(high-low) + low;
    }
    
    @Override
    public void run() {

        Lease lease = null;
        try {
            lease = semaphore.acquire();
            
            startTime = System.currentTimeMillis();
            System.out.println(sdf.format(new Date()) + " - [" + Thread.currentThread().getId() + "] - Lock Aquired: Thread Working...");
            Thread.sleep(work);
            waitIfNecessary(); // wait until 1 sec
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (lease != null) {
                semaphore.returnLease(lease);
            }
            
            latch.countDown();
        }
    }


    /**
     * waits at least 1s per execution
     */
    public void waitIfNecessary() {
        
        long endTime = System.currentTimeMillis();
        long elapsed = endTime - startTime;
        
        if (elapsed < 1000) {
            try {
                //System.out.println(Thread.currentThread().getId() + " - waiting for: " + (1000L - elapsed) + "ms");
                Thread.sleep(1000L - elapsed);
            } catch (InterruptedException e) {
                ;
            }
        } else {
            System.out.println("[SLOW!]" + Thread.currentThread().getId() + " - elapsed : " + elapsed + "ms");
        }
    }
}
