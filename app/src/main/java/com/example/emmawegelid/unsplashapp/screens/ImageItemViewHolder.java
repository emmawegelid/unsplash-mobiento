package com.example.emmawegelid.unsplashapp.screens;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.emmawegelid.unsplashapp.R;
import com.example.emmawegelid.unsplashapp.models.Image;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartadapters.views.BindableRelativeLayout;

public class ImageItemViewHolder extends BindableRelativeLayout<Image> {

    public static final int IMAGE_TAPPED = 0;

    private Context context;

    @BindView(R.id.imageView)
    ImageView imageView;

    public ImageItemViewHolder(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public int getLayoutId() {
        return R.layout.image_item;
    }

    @Override
    public void onViewInflated() {
        super.onViewInflated();
        ButterKnife.bind(this);
    }

    @Override
    public void bind(Image image) {
        Glide.with(context)
                .load(image.urls.thumb)
                .apply(RequestOptions.centerCropTransform())
                .into(imageView);
    }

    @OnClick(R.id.rootLayout)
    public void showFullScreenImage() {
        notifyItemAction(IMAGE_TAPPED);
    }
}
