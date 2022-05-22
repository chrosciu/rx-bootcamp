package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RequiredArgsConstructor
public class GithubClient {
    private final GithubApi githubApi;

    public Flux<Repository> getUserRepositories(String username) {

        Mono<List<Repository>> repos = githubApi.getUserRepositories(username);
        return repos.flatMapMany(Flux::fromIterable);
    }

    public Flux<Branch> getUserRepositoryBranches(String username, String repo) {

        Mono<List<Branch>> branches = githubApi.getUserRepositoryBranches(username, repo);
        return branches.flatMapMany(Flux::fromIterable);
    }

    public Flux<Repository> getUsersRepositories(Flux<String> usernames) {

        // tu publisher z uzytkownikami
        Flux<Repository> usersRepositories =
                usernames
                        .log()
                        .flatMap(s -> githubApi.getUserRepositories(s))
                        .flatMap(Flux::fromIterable);

        return usersRepositories;
    }

    public Flux<String> getAllUserBranchesNames(String username) {

//        Mono<List<Repository>> repos = githubApi.getUserRepositories(username);
//
//        Flux<Repository> repositories = repos.flatMapMany(Flux::fromIterable);
//
//        Flux<String> branchesNames =
//                repositories
//                        .log()
//                        .flatMap(repository -> githubApi.getUserRepositoryBranches(username, repository.getName()))
//                        .flatMap(Flux::fromIterable)
//                        .log()
//                        .map(Branch::getName)
//                        .log()
//                        ;

        return githubApi.getUserRepositories(username)
                .flatMapMany(Flux::fromIterable)
                .flatMap(repository -> githubApi.getUserRepositoryBranches(username, repository.getName()))
                .flatMap(Flux::fromIterable)
                .map(Branch::getName)
                ;
    }


    public Flux<String> getAllUserBranchesNamesInParallel(String username) {

        return githubApi.getUserRepositories(username)
                .flatMapMany(Flux::fromIterable)
                .parallel(3)
                .runOn(Schedulers.parallel())
                .flatMap(repository -> githubApi.getUserRepositoryBranches(username, repository.getName()))
                .flatMap(Flux::fromIterable)
                .sequential()
                //.publishOn(Schedulers.single())
                .map(Branch::getName)
                ;
    }


}
