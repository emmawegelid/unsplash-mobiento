package com.example.emmawegelid.unsplashapp.screens;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

public class FullScreenPhotoFragment extends Fragment {

    public static final String TAG = FullScreenPhotoFragment.class.getSimpleName();

    private static final String PHOTO_URL_ARG = "photoUrl";

    private String photoUrl;

    @BindView(R.id.fullScreenPhotoView)
    ImageView fullScreenPhotoView;

    @BindView(R.id.loadingIndicator)
    ProgressBar loadingIndicator;

    public static FullScreenPhotoFragment newInstance(String photoUrl) {
        Bundle args = new Bundle();
        FullScreenPhotoFragment fragment = new FullScreenPhotoFragment();
        args.putString(PHOTO_URL_ARG, photoUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fullscreen_photo, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        photoUrl = getArguments().getString(PHOTO_URL_ARG);
        initUI();
    }

    private void initUI() {
        Glide.with(this)
                .load(photoUrl)
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
                .into(fullScreenPhotoView);
    }

}
