package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class GithubClient {
    private final GithubApi githubApi;

    public Flux<Repository> getUserRepositories(String username) {
        Mono<List<Repository>> mono = githubApi.getUserRepositories(username);
        Flux<Repository> flux = mono.flatMapMany(Flux::fromIterable);
        return flux;
    }

    public Flux<Branch> getUserRepositoryBranches(String username, String repo) {

        Mono<List<Branch>> mono = githubApi.getUserRepositoryBranches(username, repo);
        Flux<Branch> flux = mono.flatMapMany(Flux::fromIterable);
        return flux;
    }

    public Flux<Repository> getUsersRepositories(Flux<String> usernames) {

    /*  Flux<Repository> flux = usernames.flatMap(x -> getUserRepositories(x));
        return flux;*/
        return usernames.flatMap(user -> getUserRepositories(user));
    }

    public Flux<String> getAllUserBranchesNames(String username) {

        Flux<Repository> flux = getUserRepositories(username);
        Flux<Branch> flux2 = flux.flatMap(x -> getUserRepositoryBranches(username, x.getName()));
        Flux<String> flux3 = flux2.map(x -> x.getName());

        return flux3;
    }
}