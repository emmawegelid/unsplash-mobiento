package com.example.emmawegelid.unsplashapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.emmawegelid.unsplashapp.listeners.PhotoSearchListener;
import com.example.emmawegelid.unsplashapp.screens.FullScreenPhotoFragment;
import com.example.emmawegelid.unsplashapp.screens.SearchPhotosFragment;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements PhotoSearchListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initFragments();
    }

    private void initFragments() {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.contentFrame, SearchPhotosFragment.newInstance(), SearchPhotosFragment.TAG)
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    @Override
    public void showFullScreenPhoto(String photoUrl) {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.contentFrame, FullScreenPhotoFragment.newInstance(photoUrl), FullScreenPhotoFragment.TAG)
                .addToBackStack(FullScreenPhotoFragment.TAG)
                .commit();
        getFragmentManager().executePendingTransactions();
    }
}
