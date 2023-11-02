/*
 *  Copyright 2016-2023 Qameta Software OÜ
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.qameta.allure.jira;

import io.qameta.allure.jira.retrofit.BasicAuthInterceptor;
import io.qameta.allure.jira.retrofit.DefaultCallAdapterFactory;
import io.qameta.allure.jira.retrofit.OAuthInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Objects;

import static io.qameta.allure.util.PropertyUtils.getProperty;
import static io.qameta.allure.util.PropertyUtils.requireProperty;

/**
 * Jira Service builder.
 */
@SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
public class JiraServiceBuilder {

    private static final String JIRA_ENDPOINT = "ALLURE_JIRA_ENDPOINT";
    private static final String JIRA_USERNAME = "ALLURE_JIRA_USERNAME";
    private static final String JIRA_PASSWORD = "ALLURE_JIRA_PASSWORD";
    public static final String ALLURE_JIRA_CLIENT_ID = "ALLURE_JIRA_CLIENT_ID";
    public static final String ALLURE_JIRA_CLIENT_SECRET = "ALLURE_JIRA_CLIENT_SECRET";

    private String endpoint;
    private String username;
    private String password;

    private String clientId;

    private String clientSecret;

    public JiraServiceBuilder endpoint(final String endpoint) {
        Objects.requireNonNull(endpoint);
        this.endpoint = addSlashIfMissing(endpoint);
        return this;
    }

    public JiraServiceBuilder username(final String username) {
        Objects.requireNonNull(username);
        this.username = username;
        return this;
    }

    public JiraServiceBuilder password(final String password) {
        Objects.requireNonNull(password);
        this.password = password;
        return this;
    }

    public JiraServiceBuilder clientId(final String clientId) {
        Objects.requireNonNull(clientId);
        this.clientId = clientId;
        return this;
    }

    public JiraServiceBuilder clientSecret(final String clientSecret) {
        Objects.requireNonNull(clientSecret);
        this.clientSecret = clientSecret;
        return this;
    }

    public JiraServiceBuilder defaults() {
        String clientIdEnv = getProperty(ALLURE_JIRA_CLIENT_ID).orElse(null);
        String clientSecretEnv = getProperty(ALLURE_JIRA_CLIENT_SECRET).orElse(null);
        if (clientIdEnv != null && clientSecretEnv != null) {
            clientId(clientId);
            clientSecret(clientSecret);
        } else {
            endpoint(requireProperty(JIRA_ENDPOINT));
            username(requireProperty(JIRA_USERNAME));
            password(requireProperty(JIRA_PASSWORD));
        }
        return this;
    }

    public JiraService build() {
        final OkHttpClient client;
        if (clientId != null && clientSecret != null) {
            client = new OkHttpClient.Builder()
                    .addInterceptor(new OAuthInterceptor(clientId, clientSecret))
                    .build();
        } else {
            client = new OkHttpClient.Builder()
                    .addInterceptor(new BasicAuthInterceptor(username, password))
                    .build();
        }

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(new DefaultCallAdapterFactory<>())
                .client(client)
                .build();
        return retrofit.create(JiraService.class);
    }

    private static String addSlashIfMissing(final String endpoint) {
        final String slash = "/";
        return endpoint.endsWith(slash) ? endpoint : endpoint + slash;
    }

}
