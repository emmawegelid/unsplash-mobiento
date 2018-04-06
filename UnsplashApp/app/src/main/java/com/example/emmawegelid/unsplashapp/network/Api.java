package com.example.emmawegelid.unsplashapp.network;

import com.example.emmawegelid.unsplashapp.network.wrappers.PhotoSearchWrapper;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    @GET("search/photos?")
    Observable<PhotoSearchWrapper.Response> searchPhotos(@Query("query") String query, @Query("page") int page);

}
