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
        return Flux.create((sink) -> {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                String[] words = line.split(" ");
                for (int i = 0; i < words.length; i++) {
                    sink.next(words[i]);
                }
            }
        });
    }
}
