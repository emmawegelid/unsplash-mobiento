package com.example.emmawegelid.unsplashapp.network.wrappers;

import com.example.emmawegelid.unsplashapp.models.Photo;

import java.util.List;

public class PhotoSearchWrapper {

    public static class Response {
        public int total_pages;
        public List<Photo> results;
    }

}
