package com.example.emmawegelid.unsplashapp.screens;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.emmawegelid.unsplashapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FullScreenImageFragment extends Fragment {

    public static final String TAG = FullScreenImageFragment.class.getSimpleName();

    private static final String IMAGE_URL_ARG = "imageUrl";

    private String imageUrl;

    @BindView(R.id.fullScreenImageView)
    ImageView fullScreenImageView;

    @BindView(R.id.loadingIndicator)
    ProgressBar loadingIndicator;

    public static FullScreenImageFragment newInstance(String imageUrl) {
        Bundle args = new Bundle();
        FullScreenImageFragment fragment = new FullScreenImageFragment();
        args.putString(IMAGE_URL_ARG, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fullscreen_image, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageUrl = getArguments().getString(IMAGE_URL_ARG);
        initUI();
    }

    private void initUI() {
        Glide.with(this)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        loadingIndicator.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loadingIndicator.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(fullScreenImageView);
    }

}
