package com.example.emmawegelid.unsplashapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.emmawegelid.unsplashapp.screens.ImageSearchFragment;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

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
                .commitAllowingStateLoss();
        getFragmentManager().executePendingTransactions();
    }
}
