package firstTask;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Loader {
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        ExecutorService service = Executors.newFixedThreadPool(5);

        Set<Future<?>> futureList = new HashSet<>();
        //Разделим задачу на 4 потока, каждому потоку выделим по 20 регионов
        for (int i = 1, startRegionCode = 1; i <= 5; i++, startRegionCode += 20) {
            futureList.add(
                    service.submit(
                            new Generator(startRegionCode, i * 20, i)
                    )
            );
        }
        futureList.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        service.shutdown();
        System.out.println((System.currentTimeMillis() - start) + " ms");
    }
}
