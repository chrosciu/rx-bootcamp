package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
public class GithubClient {
    private final GithubApi githubApi;

    public Flux<Repository> getUserRepositories(String username) {
        return githubApi.getUserRepositories(username)
                .flatMapMany(Flux::fromIterable);
    }

    public Flux<Branch> getUserRepositoryBranches(String username, String repo) {
        return githubApi.getUserRepositoryBranches(username, repo)
                .flatMapMany(Flux::fromIterable);
    }

    public Flux<Repository> getUsersRepositories(Flux<String> usernames) {
        return usernames.flatMap(this::getUserRepositories);
    }

    public Flux<String> getAllUserBranchesNames(String username) {
        return getUserRepositories(username)
                .flatMap(repo -> getUserRepositoryBranches(username, repo.getName()))
                .map(Branch::getName);
    }

    public Flux<String> getAllUserBranchesNamesParallel(String username) {
        Scheduler scheduler = Schedulers.boundedElastic();
        return getUserRepositories(username)
                .subscribeOn(scheduler)
                .flatMap(repo -> getUserRepositoryBranches(username, repo.getName()))
                .map(Branch::getName);
    }
}
