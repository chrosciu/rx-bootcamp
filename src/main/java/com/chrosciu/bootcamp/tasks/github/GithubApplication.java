package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import com.chrosciu.bootcamp.tasks.input.InputUtils;
import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import reactor.core.publisher.Flux;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.InputStream;

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
    private void run() {
        log.info(">>> TASK 5");
        InputStream input = System.in;
        Flux<String> inputs = InputUtils.toFlux(input);
        Flux<Repository> repositories = githubClient.getUsersRepositories(inputs);

        repositories.subscribe(s -> log.info("{}", s));

        log.info(">>> TASK 1");
        githubClient.getUserRepositories("jasokolowska")
                .subscribe(s -> log.info("Repository: {}", s),
                        e -> log.info(e.getMessage()),
                        () -> log.info("Completed"));

        log.info(">>> TASK 2");
        githubClient.getUserRepositoryBranches("jasokolowska", "project-jdp-2112-01")
                .subscribe(s -> log.info("Repository: {}", s),
                        e -> log.info(e.getMessage()),
                        () -> log.info("Completed"));

        log.info(">>> TASK 3");
        Flux<String> users = Flux.just("joan", "andr");
        githubClient.getUsersRepositories(users)
                .subscribe(s -> log.info("Repository: {}", s),
                        e -> log.info(e.getMessage()),
                        () -> log.info("Completed"));

        log.info(">>> TASK 4");
        githubClient.getAllUserBranchesNames("jasokolowska")
                .subscribe(s -> log.info("Repository: {}", s),
                        e -> log.info(e.getMessage()),
                        () -> log.info("Completed"));
    }

    public static void main(String[] args) {
        GithubApplication githubApplication = new GithubApplication();
        try {
            githubApplication.run();
        } finally {
            githubApplication.dispose();
        }
    }
}
