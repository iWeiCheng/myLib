package com.baima.commonlibrary.imageloader;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 加载ImageView需要的参数
 * Created by cai.jia on 2016/7/14 0014.
 */
public final class ImageRequest {

    /**
     * 当用Glide加载图片时,传activity可以根据activity的生命周期来加载
     */
    private final Activity activity;

    /**
     * 当用Glide加载图片时,传fragment可以根据afragment的生命周期来加载
     */
    private final Fragment fragment;

    /**
     * 图片地址
     */
    private final String url;

    /**
     * 默认图片
     */
    private final int defaultDrawable;

    /**
     * 显示图片的View
     */
    private final View view;

    /**
     * 图片展示的宽度
     */
    private final int width;

    /**
     * 图片显示的高度
     */
    private final int height;

    /**
     * 图片下载监听
     */
    private final ImageLoadCallback callback;

    /**
     * Universal Image Loader 下载监听
     */
    private final ImageLoadingListener loadingListener;

    public ImageRequest(Builder builder) {
        this.url = builder.url;
        this.defaultDrawable = builder.defaultDrawable;
        this.view = builder.view;
        this.width = builder.width;
        this.height = builder.height;
        this.activity = builder.activity;
        this.fragment = builder.fragment;
        this.callback = builder.callback;
        this.loadingListener = builder.loadingListener;

    }

    public String url() {
        return url;
    }

    public int defaultDrawable() {
        return defaultDrawable;
    }

    public View imageView() {
        return view;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public Activity activity() {
        return activity;
    }

    public Fragment fragment() {
        return fragment;
    }

    public ImageLoadCallback callback() {
        return callback;
    }

    public ImageLoadingListener loadingListener() {
        return loadingListener;
    }

    public static class Builder {

        private String url;

        private int defaultDrawable;

        private View view;

        private int width;

        private int height;

        private ImageLoadCallback callback;

        private ImageLoadingListener loadingListener;

        private Activity activity;

        private Fragment fragment;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder defaultDrawable(int defaultDrawable) {
            this.defaultDrawable = defaultDrawable;
            return this;
        }

        public Builder uilLoaderListener(ImageLoadingListener loadingListener) {
            this.loadingListener = loadingListener;
            return this;
        }

        public Builder imageView(View view) {
            this.view = view;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Builder callback(ImageLoadCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder activity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder fragment(Fragment fragment) {
            this.fragment = fragment;
            return this;
        }

        public ImageRequest build() {
            return new ImageRequest(this);
        }
    }
}
