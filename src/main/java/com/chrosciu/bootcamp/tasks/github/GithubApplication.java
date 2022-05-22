package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import com.chrosciu.bootcamp.tasks.input.InputUtils;
import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
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
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

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
        // 1.
        Flux<Repository> repositories = githubClient.getUserRepositories("klezner");
        repositories.subscribe((r) -> log.info("Repository: {}", r.getName()) , e -> log.error(e.getClass().getName()), () -> log.info("Completed"));
        // 2.
        Flux<Branch> repositoryBranches = githubClient.getUserRepositoryBranches("klezner", "car-fleet-management-system");
        repositoryBranches.subscribe((b) -> log.info("Branch: {}", b.getName()) , e -> log.error(e.getClass().getName()), () -> log.info("Completed"));
        // 3.
        Flux<String> usernames = Flux.just("klezner", "MichalKulygin");
        Flux<Repository> usersRepositories = githubClient.getUsersRepositories(usernames);
        usersRepositories.subscribe((r) -> log.info("Repository: {}", r.getName()), e -> log.error(e.getClass().getName()), () -> log.info("Completed"));
        // 4.
        Flux<String> allBranches = githubClient.getAllUserBranchesNames("klezner");
        allBranches.doFinally(signalType -> countDownLatch.countDown())
                .subscribe((b) -> log.info("Branch: {}", b), e -> log.error(e.getClass().getName()), () -> log.info("Completed"));
        countDownLatch.await();
        // 5.
        InputStream input = System.in;
        Flux<String> username = InputUtils.toFlux(input);
        // 6.
        Flux<Repository> userRepositories = githubClient.getUsersRepositories(username);
        userRepositories.subscribe((r) -> log.info("Repository: {}", r.getName()), e -> log.error(e.getClass().getName()), () -> log.info("Completed"));
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
