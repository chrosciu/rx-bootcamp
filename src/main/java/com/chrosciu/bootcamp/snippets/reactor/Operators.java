package com.chrosciu.bootcamp.snippets.reactor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class Operators {

    private static String toLowerSync(String s) {
        return s.toLowerCase();
    }

    private static Mono<String> toLowerAsync(String s) {
        return Mono.just(s.toLowerCase());
    }

    @SneakyThrows
    public static void main(String[] args) {
        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");
        Flux<Integer> flux3 = Flux.just(1, 2, 3);

//        metdoda synchroniczna
//        Flux<String> result = flux1.map(s -> toLowerSync(s));

//        metoda asynchroniczna
//        Flux<String> result = flux1.flatMap(s -> toLowerAsync(s));

//        filtrowanie
//        Flux<String> result = flux1.filter(s -> !s.equals("A"));

//        redukowanie
//        final Mono<String> result = flux1.reduce((s1, s2) -> s1 + s2);

//        zwijanie do listy
//        final Mono<List<String>> result1 = flux1.collectList();
//        operacja odwrotna
//        Flux<String> result = result1.flatMapMany(l -> Flux.fromIterable(l));

//        pobranie pierwszych 2 elementow
//        final Flux<String> result = flux1.take(2);

//        pominiecie pierwszych dwoch elementow
//        final Flux<String> result = flux1.skip(2);

//        wynikiem jest krotka 2-elementowa z indexem i elementem
//        final Flux<Tuple2<Long, String>> result = flux1.index();

//        opoznienie elementow
//        konieczne jest wykonanie Thread.sleep() na glownym watku
//        final Flux<String> result = flux1.delayElements(Duration.ofMillis(500));

//        skladanie strumieni: najpierw pierwszy a potem drugi
//        final Flux<String> result = flux1.concatWith(flux2);

//        skladanie strumieni z zaznaczeniem czasu opoznienia
//        skladanie na zmiane
//        trzeba zwrocic uwage na czas uspienia watku glownego
//        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofMillis(500));
//        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofMillis(500));
//        final Flux<String> result = flux1.mergeWith(flux2);

//        skladanie strumie z wykorzystanie CountDownLatch
//        final CountDownLatch countDownLatch = new CountDownLatch(1);
//        final Flux<String> result = flux1.concatWith(flux2);

//        laczenie strumieni roznych typow
//        final Flux<Tuple2<String, Integer>> result = flux1.zipWith(flux3);
//        laczenie strumieni roznych typow z wyznaczeniem sposobu laczenia aby uzyskac Flux<T>
//        final Flux<String> result = flux1.zipWith(flux3).map(t -> t.getT1() + t.getT2());
//        kazda dodatkowa funkcja w strumieniu powoduje stworzenie kolejnej pary Subscriber/Publisher
//        final Flux<String> result = flux1.zipWith(flux3, (s, i) -> s + i);



//        result
//                .doFinally(signalType -> countDownLatch.countDown()) // dolozenie locka zeby uruchomic laczenie z drugim strumieniem
//                .subscribe(s -> log.info("Elem: {}", s),
//                e -> log.warn("Error: ", e),
//                () -> log.info("Completed"));

//        dodanie przy wykorzystaniu countDownLatch
//        countDownLatch.await();

//        Thread.sleep(2000);
    }

}
