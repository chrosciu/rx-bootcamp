package com.chrosciu.bootcamp.snippets.reactor;

import reactor.core.publisher.Mono;

// PROBLEM 3 - WYCIAGNIECIE STRINGA Z MONO
public class Threads3 {

    public static void main(String[] args) {
        Mono<String> mono = Mono.just("S");
//        block() blokuje wykonanie programu do czasu az Mono<String> dostanie wartosc
//        block daje szanse na zblokowanie glownego watku
//        mozna korzystac po przeniesieniu do nowego watku
        final String s = mono.block();
        process(s);
    }

    private static void process(String s) {

    }
}
