package com.example.emmawegelid.unsplashapp.network;

import com.example.emmawegelid.unsplashapp.network.wrappers.ImageSearchWrapper;

import io.reactivex.Observable;

public class ApiClient {

    private Api api;

    public ApiClient(Api api) {
        this.api = api;
    }

    public Observable<ImageSearchWrapper.Response> searchForImages(String query) {
        return api.searchForImages(query);
    }
}
