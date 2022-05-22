package com.chrosciu.bootcamp.tasks.input;

import com.chrosciu.bootcamp.tasks.github.GithubClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.io.InputStream;
import java.util.Scanner;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InputUtils {
    public static Flux<String> toFlux(InputStream inputStream) {

        Scanner scan = new Scanner(inputStream);

        Flux<String> fromInputStream = Flux.create(stringFluxSink -> {

            while (scan.hasNext()) {

                String message = scan.nextLine();

                if (message.equals("exxit")) {
                    break;
                }

                stringFluxSink.next(message);
            }
        });

        return fromInputStream;
    }

    public static void main(String[] args) {

        Flux<String> newInput = toFlux(System.in);
        newInput.subscribe(r -> log.info("line: {}", r), e -> log.error("error: ", e));
    }


}
