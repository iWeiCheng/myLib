package com.baima.commonlibrary.imageloader;

import com.baima.commonlibrary.imageloader.glide.GlideImageLoader;
import com.baima.commonlibrary.imageloader.uil.UILImageLoader;

/**
 * Created by cai.jia on 2016/7/14 0014.
 */
public class ImageLoaderFactory {

    public static ImageLoader getImageLoader() {
        return GlideImageLoader.getInstance();
    }
}
