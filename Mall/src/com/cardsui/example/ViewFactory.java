package com.cardsui.example;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.iliker.mall.R;

/**
 * ImageView创建工厂
 */
class ViewFactory {

    /**
     * 获取ImageView视图的同时加载显示url
     */
    static ImageView getImageView(Context context, String url) {
        ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(
                R.layout.view_banner, null);
        ImageLoader.getInstance().displayImage(url, imageView);
        return imageView;
    }
}
