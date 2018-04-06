package com.example.emmawegelid.unsplashapp.screens;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.emmawegelid.unsplashapp.R;
import com.example.emmawegelid.unsplashapp.models.Photo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartadapters.views.BindableRelativeLayout;

public class PhotoItemViewHolder extends BindableRelativeLayout<Photo> {

    public static final int PHOTO_TAPPED = 0;

    private Context context;

    @BindView(R.id.photoImageView)
    ImageView photoImageView;

    public PhotoItemViewHolder(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.photo_item;
    }

    @Override
    public void onViewInflated() {
        super.onViewInflated();
        ButterKnife.bind(this);
    }

    @Override
    public void bind(Photo photo) {
        Glide.with(context)
                .load(photo.urls.thumb)
                .apply(RequestOptions.centerCropTransform())
                .into(photoImageView);
    }

    @OnClick(R.id.rootLayout)
    public void showFullScreenImage() {
        notifyItemAction(PHOTO_TAPPED);
    }
}
