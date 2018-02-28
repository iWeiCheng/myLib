package com.baima.commonlibrary.imageloader.glide;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.ImageView;

import com.baima.commonlibrary.imageloader.ImageLoader;
import com.baima.commonlibrary.imageloader.ImageRequest;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

/**
 * Glide图片加载器
 * Created by cai.jia on 2016/8/1 0001.
 */

public class GlideImageLoader implements ImageLoader {

    private GlideImageLoader() {

    }

    private static volatile GlideImageLoader instance;

    public static GlideImageLoader getInstance() {
        if (instance == null) {
            synchronized (GlideImageLoader.class) {
                if (instance == null) {
                    instance = new GlideImageLoader();
                }
            }
        }
        return instance;
    }

    @Override
    public void initialize(Context context) {

    }

    @Override
    public void loadImage(ImageRequest imageRequest) {
        Fragment fragment = imageRequest.fragment();
        Activity activity = imageRequest.activity();
        int width = imageRequest.width();
        if (width == 0)
            width = imageRequest.imageView().getWidth();

        int height = imageRequest.height();
        if (height == 0)
            height = imageRequest.imageView().getWidth();
        String url = imageRequest.url();
        int defaultDrawable = imageRequest.defaultDrawable();
        ImageView imageView = (ImageView) imageRequest.imageView();

        RequestManager requestManager;
        if (activity != null) {
            requestManager = Glide.with(activity);

        } else {
            requestManager = Glide.with(fragment);
        }

        if (!TextUtils.isEmpty(url)) {
            DrawableRequestBuilder<String> requestBuilder = requestManager.load(url)
                    .centerCrop()
                    .placeholder(defaultDrawable)
                    .error(defaultDrawable)
                    .crossFade();

            if (width != 0 && height != 0) {
                requestBuilder.override(width, height)
                        .into(imageView);
            }

        }
    }
}
