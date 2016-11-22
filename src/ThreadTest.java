import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by luciano on 17/11/16.
 */
public class ThreadTest {

    public static void main(String []a) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        Runnable cmd = new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        ArrayList<Callable<Long>> calls = new ArrayList<>();
        Callable<Long> c1 = new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                System.out.println(Thread.currentThread().getName());
                return System.nanoTime();
            }
        };
        calls.add(c1);
        calls.add(c1);
        calls.add(c1);
        List<Future<Long>> times = executorService.invokeAll(calls);
        for (Future<Long> f: times ) {
            System.out.println(f.get());
        }
//        for (int i = 0; i < 12; i++) {
//            executorService.execute(cmd);
//        }
        executorService.shutdown();
    }
}
