package com.example.emmawegelid.unsplashapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.emmawegelid.unsplashapp.listeners.ImageSearchListener;
import com.example.emmawegelid.unsplashapp.screens.FullScreenImageFragment;
import com.example.emmawegelid.unsplashapp.screens.ImageSearchFragment;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ImageSearchListener {

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
                .add(R.id.contentFrame, ImageSearchFragment.newInstance(), ImageSearchFragment.TAG)
                .commit();
        getFragmentManager().executePendingTransactions();
    }

    @Override
    public void openFullScreenImage(String imageUrl) {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.contentFrame, FullScreenImageFragment.newInstance(imageUrl), FullScreenImageFragment.TAG)
                .addToBackStack(FullScreenImageFragment.TAG)
                .commit();
        getFragmentManager().executePendingTransactions();
    }
}
