package com.jalkal;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App {

    private static String cache;

    public static void main(String[] args) {
        App app = new App();
        int numberOfDatadips = 300;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfDatadips, r -> {Thread t = new Thread(r); t.setDaemon(true); return t;});
        long start = System.currentTimeMillis();
        List<CompletableFuture<Void>> collect = IntStream.range(0, numberOfDatadips)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> app.executeDataDip(), executor))
                .map(future -> future.thenAccept(App::callBack))
                .collect(Collectors.toList());

        collect.stream().map(CompletableFuture::join).collect(Collectors.toList());
        System.out.println("Finished in " + (System.currentTimeMillis() - start) + "ms");
    }

    public static void callBack(Object object){
        System.out.println(object);
    }

    public String executeDataDip(){
        String result;
        if (cache == null) {
            cache = "value";
            result = "calling 3rt party";
        } else {
            result = "value from cache";
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
