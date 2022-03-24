package com.chrosciu.bootcamp.snippets.reactor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

// PROBLEM 1 - BLOKOWANIE W GENEROWANIU DANYCH - subscribeOn()
@Slf4j
public class Threads {

    @SneakyThrows
    public static void main(String[] args) {

        final Scheduler scheduler = Schedulers.elastic();
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        log.info("Before create");
        Flux<String> flux = Flux.create(sink -> {
//            przyklad z blokowaniem watku
            log.info("Before elements");
            List<String> elements = getElements();
            for (String element : elements) {
                log.info("Before sending: {}", element);
                sink.next(element);
                log.info("After sending: {}", element);
            }
//            przyklad nieblokujacy
//            log.info("Before A");
//            sink.next("A");
//            log.info("After A");
//            log.info("Before B");
//            sink.next("B");
//            log.info("After B");
            log.info("Before complete");
            sink.complete();
            log.info("After complete");
        });
        log.info("After create");
        log.info("Before subscribe");
//        zrownoleglenie i wyslanie pobierania danych do innych watkow
//        wykorzystanie Schedulera
        flux.subscribeOn(scheduler)
                .doFinally(signalType -> countDownLatch.countDown())
                .subscribe(s -> log.info("Elem: {}", s),
                e -> log.warn("Error: ", e),
                () -> log.info("Completed"));
        log.info("After subscribe");
        countDownLatch.await();
    }

//    metoda blokujaca produkujaca dane
    @SneakyThrows
    private static List<String> getElements() {
        log.info("Blocking...");
        Thread.sleep(1000);
        return Arrays.asList("X", "Y", "X");
    }

}
