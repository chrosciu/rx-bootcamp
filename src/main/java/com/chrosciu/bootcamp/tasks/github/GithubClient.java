package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Slf4j
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
                return usernames
                        .flatMap(this::getUserRepositories)
                        .onErrorContinue((error, user) -> log.error(String.format("Cannot download data. Make sure the username is correct. Err: %s", error.getMessage())));
    }

    public Flux<String> getAllUserBranchesNames(String username) {
        return getUserRepositories(username)
                .map(r -> getUserRepositoryBranches(username, r.getName()))
                .flatMap(b -> b.map(Branch::getName).parallel())
                .distinct();
    }

}
