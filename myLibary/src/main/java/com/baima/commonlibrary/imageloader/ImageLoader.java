package com.baima.commonlibrary.imageloader;

import android.content.Context;

/**
 * Created by cai.jia on 2016/7/14 0014.
 */
public interface ImageLoader {

    /**
     * 初始化配置
     *
     * @param context 上下文
     */
    void initialize(Context context);

    /**
     * 加载图片
     *
     * @param imageRequest 加载图片需要的信息
     */
    void loadImage(ImageRequest imageRequest);

}
