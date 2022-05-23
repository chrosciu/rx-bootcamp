package com.chrosciu.bootcamp.tasks.input;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.Scanner;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InputUtils {
    public static Flux<String> toFlux(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);

        Flux<String> flux = Flux.create(fluxSink -> {
            fluxSink.next(scanner.next());
            fluxSink.complete();
        });
        return flux;
    }
}
