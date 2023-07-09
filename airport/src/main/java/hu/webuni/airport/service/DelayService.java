package hu.webuni.airport.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class DelayService {

    private Random random = new Random();

    @Async
    public Integer getDelay(long flightId) {
        System.out.println("DelayService.getDelay called at thread " + Thread.currentThread().getName());
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        return random.nextInt(0, 1800);
    }
}
