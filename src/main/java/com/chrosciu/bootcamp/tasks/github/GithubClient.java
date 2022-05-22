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
        Mono<List<Repository>> repos = githubApi.getUserRepositories(username);
        return repos.flatMapMany(l -> Flux.fromIterable(l));
    }

    public Flux<Branch> getUserRepositoryBranches(String username, String repo) {
        Mono<List<Branch>> branches = githubApi.getUserRepositoryBranches(username, repo);
        return branches.flatMapMany(l -> Flux.fromIterable(l));
    }

    public Flux<Repository> getUsersRepositories(Flux<String> usernames) {
        return usernames.flatMap(user -> getUserRepositories(user));
    }

    public Flux<String> getAllUserBranchesNames(String username) {
        return getUserRepositories(username)
                .flatMap(repo -> getUserRepositoryBranches(username, repo.getName()))
                .map(Branch::getName)
                .distinct();
    }
}
