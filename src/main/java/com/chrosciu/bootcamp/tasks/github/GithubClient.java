package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;

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
        Flux<Repository> flux = usernames.flatMap(this::getUserRepositories);
        return flux;
    }

    public Flux<String> getAllUserBranchesNames(String username) {
        Flux<String> flux = getUserRepositories(username)
                .flatMap(repo -> getUserRepositoryBranches(username, repo.getName()))
                .map(Branch::getName);
        return flux;
    }

    public Flux<String> getAllUserBranchesNamesParallel(String username) {
        Scheduler scheduler = Schedulers.boundedElastic();
        Flux<String> flux = getUserRepositories(username)
                .subscribeOn(scheduler)
                .flatMap(repo -> getUserRepositoryBranches(username, repo.getName()))
                .map(Branch::getName);
        return flux;
    }
}
