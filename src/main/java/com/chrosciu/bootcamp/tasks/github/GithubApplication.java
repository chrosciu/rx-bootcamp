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
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

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
        // Zadanie 1.a: Flux<Repository> getUserRepositories(String username)
        // - ma zwracać wszystkie repozytoria użytkownika GH o podanej nazwie
        testGetUserRepositories();

        // Zadanie 1.b: Flux<Repository> getUsersRepositories(Publisher<String> usernames)
        // - ma zwracać wszystkie repozytoria podanych użytkowników GH (użytkownicy są przekazani jako strumień)
        testGetUsersRepositories();

        // Zadanie 1.c: Flux<Branch> getUserRepositoryBranches(String username, String repo)
        // - ma zwracać nazwy wszystkich branch w repozytorium repo należącym do użytkownika username
        testGetUserRepositoryBranches();

        // Zadanie 1.d: Flux<String> getAllUserBranches(String username)
        // - ma zwracać nazwy wszystkich branchy we wszystkich repozytoriach użytkownika GH o podanej nazwie
        testGetAllUserBranches();

        // Zadanie 4: Użyć metod getAllUserBranches oraz InputUtils.toFlux() w metodzie run() tak aby program “w kółko”:
        // - pobierał z klawiatury nazwę użytkownika GH
        // - wyświetlał na konsoli wszystkie repozytoria tego użytkownika
        testGetUserBranchesFromInputStream();

        // Zadanie 5: Zmienić implementację metody getAllUserBranches tak aby zrównoleglić odpytywanie GH API o nazwy branchy
        // (zakładamy że kolejność w jakiej te nazwy zostaną zwrócone z metody nie ma znaczenia)
        testGetAllUserBranchesParallel();
    }

    private void testGetAllUserBranchesParallel() throws InterruptedException {
        Flux<String> flux = Flux.just("chrosciu", "baranosiu");
        CountDownLatch latch = new CountDownLatch(2);
        flux.subscribe(user -> {
            githubClient.getAllUserBranchesNamesParallel(user) // na potrzeby zadania utworzona nowa metoda
                    .subscribe(branch -> log.info("User: {} Branch: {}", user, branch),
                            error -> log.error("no user {} ", user),
                            () -> {
                                log.info("done");
                                latch.countDown();
                            }
                    );
        });
        latch.await();
    }

    private void testGetUserBranchesFromInputStream() {
        System.out.println("Ready /koniec przez znak EOF, w Linuxie Ctrl+D, w Windowsie Ctrl+Z + ENTER/");
        InputStream inputStream = new BufferedInputStream(System.in);
        Flux<String> flux = InputUtils.toFlux(inputStream);
        flux.subscribe(user -> {
            githubClient.getAllUserBranchesNames(user)
                    .subscribe(branch -> log.info("Branch: {}", branch)
                            , error -> log.error("no user {} ", user)
                            , () -> log.info("done"));
        });
    }

    private void testGetAllUserBranches() {
        Flux<String> flux = githubClient.getAllUserBranchesNames("baranosiu");
        flux.subscribe(branch -> log.info("Branch: {}", branch));
    }

    private void testGetUserRepositoryBranches() {
        Flux<Branch> flux = githubClient.getUserRepositoryBranches("baranosiu", "rx-bootcamp");
        flux.subscribe(branch -> log.info("Branch: {} ", branch.getName()));
    }

    private void testGetUsersRepositories() {
        Flux<String> users = Flux.just("baranosiu", "chrosciu");
        Flux<Repository> flux = githubClient.getUsersRepositories(users);
        flux.subscribe(repo -> log.info("Repository {}", repo.getName()));
    }

    private void testGetUserRepositories() {
        Flux<Repository> flux = githubClient.getUserRepositories("baranosiu");
        flux.subscribe(repo -> log.info("Repository: {} ", repo.getName()));
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
