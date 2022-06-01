package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
public class GithubClient {
    private final GithubApi githubApi;

    public Flux<Repository> getUserRepositories(String username) {
        return githubApi.getUserRepositories(username)
                .flatMapMany(Flux::fromIterable);
    }

    public Flux<Branch> getUserRepositoryBranches(String username, String repo) {
        return githubApi.getUserRepositoryBranches(username,repo)
                .flatMapMany(Flux::fromIterable);
    }

    public Flux<Repository> getUsersRepositories(Flux<String> usernames) {
        return usernames.flatMap(username -> getUserRepositories(username));
    }

    public Flux<String> getAllUserBranchesNames(String username) {
        return getUserRepositories(username)
                .flatMap(repository -> getUserRepositoryBranches(username,repository.getName()))
                .map(Branch::getName)
                .distinct();
    }
}
