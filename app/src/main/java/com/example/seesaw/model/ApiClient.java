package com.example.seesaw.model;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://api.openai.com/";

    public static ChatGPTApi getChatGPTApi() {
        // OkHttpClient를 생성하고 타임아웃 설정 적용
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃을 30초로 설정
                .readTimeout(30, TimeUnit.SECONDS)    // 읽기 타임아웃을 30초로 설정
                .writeTimeout(30, TimeUnit.SECONDS)   // 쓰기 타임아웃을 30초로 설정
                .build();

        // Retrofit 인스턴스 생성 및 OkHttpClient 설정 적용
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client) // OkHttpClient를 Retrofit에 설정
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ChatGPTApi.class);
    }
}