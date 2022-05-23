package com.chrosciu.bootcamp.tasks.input;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;

import java.io.InputStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InputUtils {
    public static Flux<String> toFlux(InputStream inputStream) {
        return Flux
                .create(sink -> {
                    Scanner scanner = new Scanner(inputStream);
                    while (scanner.hasNext()) {
                        sink.next(scanner.nextLine());
                    }
                });
}
