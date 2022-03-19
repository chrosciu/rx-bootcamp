package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GithubClient {
    private final GithubApi githubApi;

    public Flux<Repository> getUserRepositories(String username) {
        Flux<Repository> repositoryFlux = githubApi.getUserRepositories(username).flatMapMany(Flux::fromIterable);
        return repositoryFlux;
    }

    public Flux<Branch> getUserRepositoryBranches(String username, String repo) {
        Flux<Branch> branchFlux = githubApi.getUserRepositoryBranches(username, repo).flatMapMany(Flux::fromIterable);
        return branchFlux;
    }

    public Flux<Repository> getUsersRepositories(Flux<String> usernames) {
        //TODO: Implement
        return null;
    }

    public Flux<String> getAllUserBranchesNames(String username) {
        //TODO: Implement
        return null;
    }
}
