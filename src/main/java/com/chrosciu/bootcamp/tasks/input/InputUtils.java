package com.chrosciu.bootcamp.tasks.input;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.Scanner;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InputUtils {
    public static Flux<String> toFlux(InputStream inputStream) {
        Scanner input = new Scanner(inputStream);
        String s = input.next();
        Flux<String> flux = Flux.create(sink -> {
            new Thread(() -> {
                IntStream.range(0, 5)
                        .forEach(i -> sink.next(s));
                sink.complete();
            }).start();
        });
        return flux;
    }
}
