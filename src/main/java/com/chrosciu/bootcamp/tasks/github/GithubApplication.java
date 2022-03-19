package com.chrosciu.bootcamp.tasks.github;

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
    private void run() {

        githubClient.getUserRepositories("magikabdul").subscribe(
                repository -> log.info("Repo: {}", repository),
                error -> log.warn("Error: {}", error.getMessage()),
                () -> log.info("Completed!")
        );

        System.out.println("\n\n\n\n\n");

        githubClient.getUserRepositoryBranches("magikabdul", "configaro")
                .subscribe(
                        branch -> log.info("Branch: {}", branch),
                        error -> log.warn("Error: {}", error.getMessage()),
                        () -> log.info("Completed!!")
                );

        System.out.println("\n\n\n\n\n");

        githubClient.getUsersRepositories(Flux.just("magikabdul", "chrosciu"))
                .subscribe(
                        repository -> log.info("Repository: {}", repository),
                        error -> log.warn("Error: {}", error.getMessage()),
                        () -> log.info("Completed!!!")
                );

        System.out.println("\n\n\n\n\n");

        githubClient.getAllUserBranchesNames("magikabdul")
                .subscribe(
                        name -> log.info("Branch: {}", name),
                        error -> log.warn("Error: {}", error.getMessage()),
                        () -> log.info("Completed!!!")
                );
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
