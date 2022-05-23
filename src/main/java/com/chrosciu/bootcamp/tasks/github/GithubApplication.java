package com.chrosciu.bootcamp.tasks.github;

import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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
        //TASK 1: Flux<Repository> getUserRepositories(String username)

        /*githubClient.getUserRepositories("jegor-chomczuk")
                .subscribe(r -> {
                    log.info("Repository: {}", r.getName());
        });*/

        //TASK 2: Flux<Branch> getUserRepositoryBranches(String username, String repo)

        /*githubClient.getUserRepositoryBranches("jegor-chomczuk", "GatewayService")
                .subscribe(b -> {
                    log.info("Branches: {}", b.getName());
                });*/


        //TASK 3: Flux<Repository> getUsersRepositories(Flux<String> usernames)

        /*Flux<String> usernames = Flux.just("jegor-chomczuk");
        githubClient.getUsersRepositories(usernames)
                .subscribe(r -> {
                    log.info("Repository: {}", r.getName());
        });*/

        //TASK 4: Flux<String> getAllUserBranchesNames(String username)

        /*githubClient.getAllUserBranchesNames("jegor-chomczuk")
                .subscribe(s ->
                    log.info("Branch: {}", s),
                    e -> log.error("Error: {}", e.getMessage()),
                    () -> log.info("Completed"));*/

        //TASK 5: InputUtils.toFlux

        /*InputStream input = System.in;
        Flux<String> inputs = InputUtils.toFlux(input);
        Flux<Repository> repositories = githubClient.getUsersRepositories(inputs);

        repositories.subscribe(r -> {
            log.info("Repository: {}", r);
        });*/
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
