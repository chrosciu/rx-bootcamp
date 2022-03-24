package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class GithubClient {
    private final GithubApi githubApi;

    public Flux<Repository> getUserRepositories(String username) {
        Mono<List<Repository>> results = githubApi.getUserRepositories(username);
        return results.flatMapMany(Flux::fromIterable);
    }

    public Flux<Branch> getUserRepositoryBranches(String username, String repo) {
        Mono<List<Branch>> branches = githubApi.getUserRepositoryBranches(username, repo);
        return branches.flatMapMany(Flux::fromIterable);
    }

    public Flux<Repository> getUsersRepositories(Flux<String> usernames) {
        return usernames.flatMap(this::getUserRepositories);
    }

    public Flux<String> getAllUserBranchesNames(String username) {
        return getUsersRepositories(Flux.just("SebastianRoslon"))
                .flatMap(r -> getUserRepositoryBranches(username, r.getName()))
                .distinct()
                .map(Branch::getName);

    }
}
