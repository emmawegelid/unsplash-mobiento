package com.example.emmawegelid.unsplashapp.network.wrappers;

import com.example.emmawegelid.unsplashapp.models.Image;

import java.util.List;

public class ImageSearchWrapper {

    public static class Response {
        public int total;
        public int total_pages;
        public List<Image> results;
    }

}
