package com.baima.commonlibrary.imageloader;

/**
 * Created by cai.jia on 2016/7/14 0014.
 */
public interface ImageLoadCallback {

    void onSuccess(Object ...obj);

    void onFailure(Object ...obj);

}
