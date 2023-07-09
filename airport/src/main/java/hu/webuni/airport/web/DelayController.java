package hu.webuni.airport.web;

import hu.webuni.airport.service.DelayService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DelayController {

    private final DelayService delayService;

    @GetMapping("/flights/{id}/delay")
    @Async
    public CompletableFuture<Integer> getDelayforFlight(@PathVariable long id) {
        System.out.println("DelayService.getDelayForFlight called at thread " + Thread.currentThread().getName());

        return CompletableFuture.completedFuture(delayService.getDelay(id));
    }
}
