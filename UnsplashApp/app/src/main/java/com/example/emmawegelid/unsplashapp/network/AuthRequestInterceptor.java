package com.example.emmawegelid.unsplashapp.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthRequestInterceptor implements Interceptor {

    private static final String ACCESS_TOKEN = "676028b042b7aa90dc92248fb333b6ecd89e03bea289e107e77e47d8b6b25c42";
    private static final String API_VERSION = "v1";

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();

        Request.Builder builder = original.newBuilder();
        builder.header("Authorization", "Client-ID " + ACCESS_TOKEN);
        builder.header("Accept-Version", API_VERSION);

        return chain.proceed(builder.build());
    }

}
