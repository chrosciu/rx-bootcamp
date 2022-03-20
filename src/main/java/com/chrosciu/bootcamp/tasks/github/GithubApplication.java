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
        //zadanie 1
        Flux<Repository> flux1 = githubClient.getUserRepositories("Jacentus");
        flux1.subscribe(i -> System.out.println(i),
                error -> System.out.println("Error " + error),
                () -> System.out.println("Completed successfully"));
        System.out.println("Zadanie 2 ****************************************");
        //zadanie 2
        Flux<Branch> flux2 = githubClient.getUserRepositoryBranches("Jacentus", "rx-bootcamp");
        flux2.subscribe(i -> System.out.println(i),
                error -> System.out.println("Error " + error),
                () -> System.out.println("Completed successfully"));
        System.out.println("Zadanie 3 ****************************************");
        //zadanie 3
        Flux flux3 = Flux.just("Jacentus", "chrosciu");
        Flux<Repository> flux4 = githubClient.getUsersRepositories(flux3);
        flux4.subscribe(i -> System.out.println(i),
                error -> System.out.println("Error " + error),
                () -> System.out.println("Completed successfully"));
        System.out.println("Zadanie 4 ****************************************");
        //zadanie 4
        githubClient.getAllUserBranchesNames("Jacentus")
                .subscribe(i -> System.out.println(i),
                        error -> System.out.println("Error " + error),
                        () -> System.out.println("Completed successfully"));
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
