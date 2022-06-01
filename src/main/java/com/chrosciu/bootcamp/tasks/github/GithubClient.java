package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GithubClient {
    private final GithubApi githubApi;

    public Flux<Repository> getUserRepositories(String username) {
        return githubApi.getUserRepositories(username)
                .flatMapMany(Flux::fromIterable);
    }

    public Flux<Branch> getUserRepositoryBranches(String username, String repo) {
        return githubApi
                .getUserRepositoryBranches(username, repo)
                .flatMapMany(Flux::fromIterable);
    }

    public Flux<Repository> getUsersRepositories(Flux<String> usernames) {
        return usernames
                .flatMap(githubApi::getUserRepositories)
                .flatMap(Flux::fromIterable);
    }

    public Flux<String> getAllUserBranchesNames(String username) {
        return githubApi.getUserRepositories(username)
                .flatMapMany(Flux::fromIterable)
                .flatMap(repo -> githubApi.getUserRepositoryBranches(username, repo.getName()))
                .flatMap(Flux::fromIterable)
                .map(branch -> branch.getName());
    }
}
