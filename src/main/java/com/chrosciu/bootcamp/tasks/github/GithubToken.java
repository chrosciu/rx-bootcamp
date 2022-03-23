package com.chrosciu.bootcamp.tasks.github;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.Properties;

public class GithubToken {
    private static final Properties properties;
    public static final String TOKEN;

    static {
        properties = new Properties();
        loadProperties();
        TOKEN = properties.getProperty("github.token");
        if (null == TOKEN) {
            throw new RuntimeException("Cannot read github token");
        }
    }

    @SneakyThrows
    private static void loadProperties() {
        try (final InputStream stream = GithubToken.class.getResourceAsStream("/github.properties.example")) {
            properties.load(stream);
        }
    }
}
