package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import retrofit2.HttpException;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class GithubClient {
    private final GithubApi githubApi;

    public Flux<Repository> getUserRepositories(String username) {
        Mono<List<Repository>> userRepositories = githubApi.getUserRepositories(username);
        return userRepositories.flatMapMany(Flux::fromIterable);
    }

    public Flux<Branch> getUserRepositoryBranches(String username, String repo) {
        Mono<List<Branch>> userRepositoryBranches = githubApi.getUserRepositoryBranches(username, repo);
        return userRepositoryBranches.flatMapMany(Flux::fromIterable);
    }

    public Flux<Repository> getUsersRepositories(Flux<String> usernames) {
        return usernames.flatMap(username -> getUserRepositories(username)
                .doOnError(e -> log.info(e.toString()))
                .onErrorResume(e -> Mono.empty()));
    }

    public Flux<String> getAllUserBranchesNames(String username) {
        return getUserRepositories(username)
                .flatMap(repository -> getUserRepositoryBranches(username, repository.getName()))
                .delayElements(Duration.ofMillis(1))
                .map(Branch::getName);
    }
}
