package com.baima.commonlibrary.imageloader.uil;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.baima.commonlibrary.R;
import com.baima.commonlibrary.imageloader.ImageLoader;
import com.baima.commonlibrary.imageloader.ImageRequest;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


/**
 * UIL 图片加载
 * Created by cai.jia on 2016/7/28 0028.
 */

public class UILImageLoader implements ImageLoader {

    private UILImageLoader() {

    }

    private static volatile UILImageLoader instance;

    public static UILImageLoader getInstance() {
        if (instance == null) {
            synchronized (UILImageLoader.class) {
                if (instance == null) {
                    instance = new UILImageLoader();
                }
            }
        }
        return instance;
    }

    @Override
    public void initialize(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(5) // default
                .threadPriority(Thread.NORM_PRIORITY) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(8 * 1024 * 1024))
                .memoryCacheSize(8 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(context)) // default
                .writeDebugLogs()
                .build();
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config);
    }

    @Override
    public void loadImage(ImageRequest req) {
        View view = req.imageView();
        if (!(view instanceof ImageView)) {
            return;
        }
        ImageView imageView = (ImageView) view;
        int width = req.width();
        int height = req.height();
        String url = req.url();
        final ImageLoadingListener callback = req.loadingListener();
        final int defaultImage = req.defaultDrawable();

        ImageAware aware = new ImageViewAware(imageView);
        ImageSize imageSize = null;
        if (width != 0 && height != 0) {
            imageSize = new ImageSize(width, height);
        }
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(url, aware, getTransparentOptions().build(),
                imageSize, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        setBackground(view, defaultImage);
                        if (callback != null) {
                            callback.onLoadingStarted(imageUri, view);
                        }
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        setBackground(view, defaultImage);
                        if (callback != null) {
                            callback.onLoadingFailed(imageUri, view, failReason);
                        }
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        if (loadedImage != null) {
                            if (view != null) {
                                view.setBackgroundDrawable(null);
                            }
                        } else {
                            setBackground(view, defaultImage);
                        }
                        if (callback != null) {
                            callback.onLoadingComplete(imageUri, view, loadedImage);
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        setBackground(view, defaultImage);
                        if (callback != null) {
                            callback.onLoadingCancelled(imageUri, view);
                        }
                    }
                }, null);
    }

    private static void setBackground(View view, int resId) {
        if (view != null) {
            view.setBackgroundResource(resId);
        }
    }

    private static DisplayImageOptions.Builder getTransparentOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.shape_transparent)
                .showImageOnFail(R.drawable.shape_transparent)
                .showImageForEmptyUri(R.drawable.shape_transparent)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new FadeInBitmapDisplayer(300));
    }
}
