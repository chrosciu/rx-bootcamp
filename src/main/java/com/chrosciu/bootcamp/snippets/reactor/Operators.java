package com.chrosciu.bootcamp.snippets.reactor;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Operators {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(Operators.class);
    static Flux<String> flux = Flux.just("A", "B", "C");

    static Flux<String> anotherFlux = Flux.just("D", "E", "F");

    static Flux<Long> numbers = Flux.just(1L, 2L);

    private static String toLower(String s) {
        return s.toLowerCase();
    }

    private static Mono<String> toLowerAsync(String s) {
        return Mono.fromCallable(s::toLowerCase);
    }

    static class Map {
        public static void main(String[] args) {
            Flux<String> mapped = flux.map(Operators::toLower);
            mapped.subscribe(s -> log.info("Element received: {}", s),
                    e -> log.warn("Error occurred: ", e),
                    () -> log.info("Stream completed"));
        }
    }

    static class FlatMap {
        public static void main(String[] args) {
            Flux<String> mapped = flux.flatMap(Operators::toLowerAsync)
                    .log();
            mapped.subscribe(s -> log.info("Element received: {}", s),
                    e -> log.warn("Error occured: ", e),
                    () -> log.info("Stream completed"));
        }
    }

    static class Filter {
        public static void main(String[] args) {
            Flux<String> filtered = flux.filter(s -> s.equals("A"));
            filtered.subscribe(s -> log.info("Element received: {}", s),
                    e -> log.warn("Error occured: ", e),
                    () -> log.info("Stream completed"));
        }
    }

    static class Reduce {
        public static void main(String[] args) {
            Mono<String> reduced = flux.reduce((s, s2) -> s + "," + s2);
            reduced.subscribe(s -> log.info("Element received: {}", s),
                    e -> log.warn("Error occured: ", e),
                    () -> log.info("Stream completed"));
        }
    }

    static class CollectList {
        public static void main(String[] args) {
            Mono<List<String>> collected = flux.collectList();
            //collectMap
            collected.subscribe(s -> log.info("Element received: {}", s),
                    e -> log.warn("Error occured: ", e),
                    () -> log.info("Stream completed"));
        }
    }

    static class Take {
        public static void main(String[] args) {
            Flux<String> firstTwo = flux.take(2);
            //skip, takeLast, skipLast
            firstTwo.subscribe(s -> log.info("Element received: {}", s),
                    e -> log.warn("Error occured: ", e),
                    () -> log.info("Stream completed"));
        }
    }

    static class Index {
        public static void main(String[] args) {
            Flux<Tuple2<Long, String>> indexed = flux.index();
            Flux<String> indexedAndMapped = indexed.map(objects -> "" + objects.getT1() + "->" + objects.getT2())
                    .log();
            indexedAndMapped.subscribe(s -> log.info("Element received: {}", s),
                    e -> log.warn("Error occured: ", e),
                    () -> log.info("Stream completed"));
        }
    }

    static class Concat {
        public static void main(String[] args) {
            Flux<String> concatenated = flux.concatWith(anotherFlux);
            //Flux<String> concatenated = Flux.concat(flux, anotherFlux);
            concatenated.subscribe(s -> log.info("Element received: {}", s),
                    e -> log.warn("Error occured: ", e),
                    () -> log.info("Stream completed"));
        }
    }

    static class Merge {
        @SneakyThrows
        public static void main(String[] args) {
            CountDownLatch latch = new CountDownLatch(1);
            Flux<String> merged = flux.delayElements(Duration.ofMillis(500))
                    .mergeWith(anotherFlux.delayElements(Duration.ofMillis(200))).doFinally(st -> latch.countDown());
            merged.subscribe(s -> log.info("Element received: {}", s),
                    e -> log.warn("Error occured: ", e),
                    () -> log.info("Stream completed"));
            latch.await();
        }
    }

    static class Zip {
        public static void main(String[] args) {
            Flux<Tuple2<String, Long>> zipped = flux.zipWith(numbers);
            zipped.subscribe(s -> log.info("Element received: {}", s),
                    e -> log.warn("Error occured: ", e),
                    () -> log.info("Stream completed"));
        }
    }

    static class Log {
        public static void main(String[] args) {
            Flux<String> logged = flux.log();
            logged.subscribe(s -> log.info("Element received: {}", s),
                    e -> log.warn("Error occured: ", e),
                    () -> log.info("Stream completed"));
        }
    }

    static class Callbacks {
        public static void main(String[] args) {
            Flux<String> withCallbacks = flux.doFinally(signalType -> log.info("End of subscription"))
                    .doOnNext(s -> log.info("onNextCallback: {}", s));
            withCallbacks.subscribe(s -> log.info("Element received: {}", s),
                    e -> log.warn("Error occured: ", e),
                    () -> log.info("Stream completed"));
        }
    }



}
