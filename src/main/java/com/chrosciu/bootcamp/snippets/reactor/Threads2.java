package com.chrosciu.bootcamp.snippets.reactor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;


// PROBLEM 2 - BLOKOWANIE PODCZAS OPERACJI NA KAZDYM ELEMENCIE - publishOn()
@Slf4j
public class Threads2 {

    @SneakyThrows
    public static void main(String[] args) {
        final Scheduler scheduler = Schedulers.elastic();
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        log.info("Before create");
        Flux<String> flux = Flux.create(sink -> {
//            przyklad z blokowaniem watku
            log.info("Before elements");
            List<String> elements = Arrays.asList("A", "B");
            for (String element : elements) {
                log.info("Before sending: {}", element);
                sink.next(element);
                log.info("After sending: {}", element);
            }
            log.info("Before complete");
            sink.complete();
            log.info("After complete");
        });
        log.info("After create");
        log.info("Before subscribe");

//        kolejne elementy sa zapisywane
//        nastepuje blokowanie przy kazdym wykonaniu
        flux.publishOn(scheduler).doFinally(signalType -> countDownLatch.countDown())
                .subscribe(s -> save(s),
                        e -> log.warn("Error: ", e),
                        () -> log.info("Completed"));
        log.info("After subscribe");
        countDownLatch.await();

    }

    @SneakyThrows
    private static void save(String s) {
        log.info("Blocking!!!");
        Thread.sleep(1000);
        log.info(s);
    }

}
