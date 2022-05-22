package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import reactor.core.publisher.Flux;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Slf4j
public class GithubApplication {
    private final OkHttpClient client;
    private final Retrofit retrofit;
    private final GithubApi githubApi;
    private final GithubClient githubClient;

    public GithubApplication() {
        client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .addInterceptor(new GithubAuthInterceptor(GithubToken.TOKEN))
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(ReactorCallAdapterFactory.create())
                .client(client)
                .build();
        githubApi = retrofit.create(GithubApi.class);
        githubClient = new GithubClient(githubApi);
    }

    private void dispose() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }

    @SneakyThrows
    private void runGetUserRepositories() {
        String username = "AsiaMorgas";
        Flux<Repository> flux = githubClient.getUserRepositories(username);
        flux.subscribe(r -> log.info("{}",r));
    }

    @SneakyThrows
    private void runGetUsersRepositories() {
        Flux<String> usernames = Flux.just("AsiaMorgas","chrosciu");
        Flux<Repository> flux = githubClient.getUsersRepositories(usernames)
                .log();
        flux.subscribe(r -> log.info("{}",r));
    }

    @SneakyThrows
    private void runGetAllUsersBranchesNames() {
        Flux<String> flux = githubClient
                .getAllUserBranchesNames("AsiaMorgas")
                .log();
        flux.subscribe(r -> log.info("{}",r));
    }

    public static void main(String[] args) {
        GithubApplication githubApplication = new GithubApplication();
        try {
            githubApplication.runGetUserRepositories();
            githubApplication.runGetUsersRepositories();
            githubApplication.runGetAllUsersBranchesNames();
        } finally {
            githubApplication.dispose();
        }
    }
}
