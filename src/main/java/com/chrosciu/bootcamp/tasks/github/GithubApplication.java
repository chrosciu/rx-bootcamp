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
import java.util.concurrent.CountDownLatch;

@Slf4j
public class GithubApplication {
    private final OkHttpClient client;
    private final Retrofit retrofit;
    private final GithubApi githubApi;
    private final GithubClient githubClient;
    private CountDownLatch countDownLatch;

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

        countDownLatch = new CountDownLatch(1);
    }

    private void dispose() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }

    @SneakyThrows
    private void run() {
        InputStream input = System.in;
        Flux<String> inputs = InputUtils.toFlux(input);
        inputs.subscribe(m -> log.info("Input message: {}", m));

        Flux<Repository> repositories = githubClient.getUsersRepositories(inputs);
        repositories.subscribe(r -> log.info("{}", r));

//        Flux<Repository> userRepositories = githubClient.getUserRepositories("chrosciu");
//        Flux<String> userRepositories = githubClient.getAllUserBranchesNames("chrosciu");

//        userRepositories.subscribe(repository -> log.info("Repository: {}", repository),
//                error -> log.warn("Error: {} ", error),
//                () -> log.info("Completed"));

//        Flux<Branch> repositoryBranches = githubClient.getUserRepositoryBranches("chrosciu", "2021-11-bootcamp-jpa");

//        userRepositories
//                .doFinally(signalType -> countDownLatch.countDown())
//                .subscribe(branch -> log.info("Branch: {}", branch),
//                error -> log.warn("Error: {} ", error),
//                () -> log.info("Completed"));

//        countDownLatch.await();
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
