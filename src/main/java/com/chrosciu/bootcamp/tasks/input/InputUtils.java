package com.chrosciu.bootcamp.tasks.input;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.io.InputStream;
import java.util.Scanner;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InputUtils {
    public static Flux<String> toFlux(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        return Flux.create((FluxSink<String> sink) ->{
                while(scanner.hasNext()){
                    sink.next(scanner.next());
        }});

    }
}
