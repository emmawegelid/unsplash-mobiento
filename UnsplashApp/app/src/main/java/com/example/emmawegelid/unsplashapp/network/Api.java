package com.example.emmawegelid.unsplashapp.network;

import com.example.emmawegelid.unsplashapp.network.wrappers.ImageSearchWrapper;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    @GET("search/photos?")
    Observable<ImageSearchWrapper.Response> searchForImages(@Query("query") String query);

}
