package com.chrosciu.bootcamp.tasks.github;

import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.Scanner;

public class InputUtils {

    public static Flux<String> toFlux(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        Flux<String> stringFlux = Flux.create(
                stringFluxSink -> {
                    stringFluxSink.next(scanner.nextLine()).complete();
                }
        );
        return stringFlux;
    }
}
