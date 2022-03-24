package com.chrosciu.bootcamp.snippets.reactor;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
public class Logging {

    public static void main(String[] args) {
//        nie wiadomo co dzialo sie z Fluxem wejsciowym
//        debugowanie strumieni jest bardzo trudne
//        warto dolozyc operator log() co spowoduje logowanie wszystkich operacji na tym strumieniu
        Flux<String> flux = Flux.just("A", "B", "C");
        Flux<String> result = flux
                .log()
                .skip(1)
                .map(String::toLowerCase);
//        wynik jest widoczny dla subskrypcji Fluxa
//        widac wynik tego co sie zadzialo
//        subskrypcja spowodowala zasubskrybowanie wszysktkich operatorow w obiekcie flux
        result.subscribe(s -> log.info("Elem: {}", s),
                e -> log.warn("Error: ", e),
                () -> log.info("Completed"));
//        subskrypcja rowniez spowoduje wykonanie wszystkich callbackow
//        result.subscribe();
    }
}
