package com.chrosciu.bootcamp.tasks.github;

import com.chrosciu.bootcamp.tasks.github.dto.Branch;
import com.chrosciu.bootcamp.tasks.github.dto.Repository;
import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import reactor.core.publisher.Flux;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.CountDownLatch;

import static com.chrosciu.bootcamp.tasks.input.InputUtils.toFlux;

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

        logHeader("START 1a");

        Flux<Repository> githubRepository = githubClient.getUserRepositories("gasniczka");
        githubRepository.subscribe(r -> log.info("{}", r));


        logHeader("START 1c");

        Flux<Branch> githubBranch = githubClient.getUserRepositoryBranches("gasniczka", "Sages_01_test_git");
        githubBranch.subscribe(r -> log.info("{}", r));


        logHeader("START 1b");

        Flux<String> users = Flux.just("chrosciu", "gasniczka");
        Flux<Repository> githubUsersRepositories = githubClient.getUsersRepositories(users);
        githubUsersRepositories.subscribe(r -> log.info("{}", r));


        logHeader("START 1d");

        Flux<String> githubUserAllBranches = githubClient.getAllUserBranchesNames("gasniczka");
        githubUserAllBranches.subscribe(r -> log.info("{}", r));


        logHeader("START 3 - enter line of text, exxit -> exit procedure");

        Flux<String> newInput = toFlux(System.in);
        newInput.subscribe(r -> log.info("line: {}", r), e -> log.error("error: ", e));


        logHeader("START 4 - enter github username, exxit -> exit procedure");
        // pobieranie danych z wejscia

        Flux<String> newInputUser = toFlux(System.in);
        newInputUser.subscribe(r -> {

            Flux<String> githubUserFromInAllBranches = githubClient.getAllUserBranchesNames(r);
            githubUserFromInAllBranches.subscribe(br -> log.info("{}", br));

        });


        logHeader("START 5");

        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<String> githubUserAllBranchesInParallel = githubClient.getAllUserBranchesNamesInParallel("gasniczka");
        githubUserAllBranchesInParallel.subscribe(r -> log.info("{}", r), e -> log.error("error", e), () -> countDownLatch.countDown());

        countDownLatch.await();

        logHeader("END");


    }

    public static void main(String[] args) {
        GithubApplication githubApplication = new GithubApplication();
        try {
            githubApplication.run();
        } finally {
            githubApplication.dispose();
        }
    }


    private static void logHeader(String message) {
        log.info("\n\n\n##### {}", message);
    }

}
