package com.example.myapplication;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = BuildConfig.API_BASE_URL;
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit =
                    new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(
                                    new okhttp3.OkHttpClient.Builder()
                                            .connectTimeout(
                                                    15, java.util.concurrent.TimeUnit.SECONDS)
                                            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                                            .callTimeout(45, java.util.concurrent.TimeUnit.SECONDS)
                                            .build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
}
