package com.baima.commonlibrary.imageloader;

import android.support.v7.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * RecyclerView Fling是不加载图片
 * Created by cai.jia on 2016/7/28 0028.
 */

public class RecyclerViewPauseScrollListener extends RecyclerView.OnScrollListener {

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
            ImageLoader.getInstance().pause();

        } else {
            ImageLoader.getInstance().resume();
        }
    }
}
