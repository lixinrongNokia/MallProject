package com.kawa.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cn.iliker.mall.R;

/**
 * titlebar
 */
@SuppressLint("InflateParams")
public class TitleBar {
    private final Context ctx;
    private TextView tv_title;
    private ImageView iv_left, iv_right;
    private boolean left = false;
    private boolean right = false;

    /**
     * 默认为左右图标都不显示
     */
    private TitleBar(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * 设置图标是否显示(true为显示)
     */
    public TitleBar(Context ctx, boolean left, boolean right) {
        this(ctx);
        this.left = left;
        this.right = right;
    }

    /**
     * 设置左边图标显示或隐藏(true显示)
     */
    private void setLeft(boolean arg) {
        this.left = arg;
        if (!left) {
            iv_left.setVisibility(View.GONE);
        } else {
            iv_left.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置右边图标显示或隐藏(true显示)
     */
    private void setRight(boolean arg) {
        this.right = arg;
        if (!right) {
            iv_right.setVisibility(View.GONE);
        } else {
            iv_right.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取视图
     */
    public View getView() {
        View v = LayoutInflater.from(ctx).inflate(R.layout.titlebar, null);
        tv_title = (TextView) v.findViewById(R.id.tv_title);
        iv_left = (ImageView) v.findViewById(R.id.iv_left);
        iv_right = (ImageView) v.findViewById(R.id.iv_right);
        setLeft(left);
        setRight(right);
        return v;
    }

    /**
     * 设置左边监听
     */
    public void setOnleftListener(View.OnClickListener l) {
        iv_left.setOnClickListener(l);
    }

    /**
     * 设置右边监听
     */
    public void setOnRightListener(View.OnClickListener l) {
        iv_right.setOnClickListener(l);
    }

    /**
     * 设置标题
     */
    public void setTitle(String title) {
        tv_title.setText(title);
    }

    /**
     * 设置标题
     */
    public void setTitle(int res) {
        tv_title.setText(res);
    }

}
