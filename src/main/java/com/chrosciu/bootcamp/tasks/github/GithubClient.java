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
        Mono<List<Repository>> listMono = githubApi.getUserRepositories(username);
        return listMono.flatMapMany(Flux::fromIterable);
    }

    public Flux<Branch> getUserRepositoryBranches(String username, String repo) {
        Mono<List<Branch>> listMono = githubApi.getUserRepositoryBranches(username,repo);
        return listMono.flatMapMany(Flux::fromIterable);
    }

    public Flux<Repository> getUsersRepositories(Flux<String> usernames) {
        return usernames.flatMap(u -> getUserRepositories(u));
    }

    public Flux<String> getAllUserBranchesNames(String username) {
        return getUserRepositories(username)
                .flatMap(repository -> getUserRepositoryBranches(username, repository.getName()))
                .map(branch -> branch.getName()).distinctUntilChanged();
    }
}
