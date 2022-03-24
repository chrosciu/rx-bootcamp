package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RequiredArgsConstructor
public class GithubClient {
    private final GithubApi githubApi;

    public Flux<Repository> getUserRepositories(String username) {
        Mono<List<Repository>> userRepositories = githubApi.getUserRepositories(username);
        return userRepositories
                .flatMapMany(repositories -> Flux.fromIterable(repositories));
    }

    public Flux<Branch> getUserRepositoryBranches(String username, String repo) {
        Mono<List<Branch>> userRepositoryBranches = githubApi.getUserRepositoryBranches(username, repo);
        return userRepositoryBranches
                .flatMapMany(branches -> Flux.fromIterable(branches));
    }

    public Flux<Repository> getUsersRepositories(Flux<String> usernames) {
        return usernames
                .flatMap(username -> getUserRepositories(username));
    }

    public Flux<String> getAllUserBranchesNames(String username) {
        return getUserRepositories(username)
                .flatMap(repository -> getUserRepositoryBranches(username, repository.getName()).subscribeOn(Schedulers.elastic()))
                .map(branch -> branch.getName()).subscribeOn(Schedulers.elastic())
                .distinct();
    }
}
