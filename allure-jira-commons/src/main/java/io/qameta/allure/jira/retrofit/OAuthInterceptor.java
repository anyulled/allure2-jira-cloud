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
package io.qameta.allure.jira.retrofit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Basic authenticator for okhttp client.
 */
public final class OAuthInterceptor implements Interceptor {

    private final String credentials;

    public OAuthInterceptor(final String clientId, final String clientSecret) {
        this.credentials = "{ \"client_id\": \"" + clientId + "\", \"client_secret\": \"" + clientSecret
                + "\" }";
    }

    @Override
    public Response intercept(final Chain chain) throws IOException {
        final Request request = chain.request();
        final Request authenticatedRequest = request.newBuilder()
                .header("Authorization", credentials).build();
        return chain.proceed(authenticatedRequest);
    }

}
