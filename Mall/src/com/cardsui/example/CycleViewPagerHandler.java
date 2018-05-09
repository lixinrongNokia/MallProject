package com.cardsui.example;

import android.os.Handler;

/**
 * 为了防止内存泄漏，定义外部类，防止内部类对外部类的引用
 */
class CycleViewPagerHandler extends Handler {
    CycleViewPagerHandler() {
    }
}