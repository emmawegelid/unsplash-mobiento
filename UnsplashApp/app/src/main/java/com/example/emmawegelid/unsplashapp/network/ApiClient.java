package com.example.emmawegelid.unsplashapp.network;

import com.example.emmawegelid.unsplashapp.network.wrappers.PhotoSearchWrapper;

import io.reactivex.Observable;

public class ApiClient {

    private Api api;

    public ApiClient(Api api) {
        this.api = api;
    }

    public Observable<PhotoSearchWrapper.Response> searchPhotos(String query, int page) {
        return api.searchPhotos(query, page);
    }
}
