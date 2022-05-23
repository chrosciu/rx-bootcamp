package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class GithubClient {
    private final GithubApi githubApi;

    public Flux<Repository> getUserRepositories(String username) {
        Mono<List<Repository>> userRepositories = githubApi.getUserRepositories(username).doOnError(s -> log.info("No such user"));
        return userRepositories.flatMapIterable(list -> list);
    }

    public Flux<Branch> getUserRepositoryBranches(String username, String repo) {
        Mono<List<Branch>> userRepositoryBranches = githubApi.getUserRepositoryBranches(username, repo);
        return userRepositoryBranches.flatMapIterable(list -> list);
    }

    public Flux<Repository> getUsersRepositories(Flux<String> usernames) {
        return usernames.flatMap(this::getUserRepositories).doOnError(username -> log.info("No repos for user " + username));
    }

    public Flux<String> getAllUserBranchesNames(String username) {
        return getUserRepositories(username)
                .flatMap(x -> getUserRepositoryBranches(username, x.getName()))
                .map(Branch::getName)
                .distinct();
    }
}
