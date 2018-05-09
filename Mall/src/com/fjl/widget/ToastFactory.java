package com.fjl.widget;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.iliker.mall.R;

import static com.iliker.application.CustomApplication.customApplication;

@SuppressLint("InflateParams")
public class ToastFactory {
    private static Toast toastStart;
    private static TextView message;

    public static Toast getMyToast(String msg) {
        if (toastStart == null) {
            View toastRoot = View.inflate(customApplication, R.layout.toast, null);
            message = (TextView) toastRoot.findViewById(R.id.toasttext);
            toastStart = new Toast(customApplication);
            toastStart.setGravity(Gravity.CENTER, Gravity.CENTER_HORIZONTAL,
                    Gravity.CENTER_VERTICAL);
            toastStart.setDuration(Toast.LENGTH_SHORT);
            toastStart.setView(toastRoot);
        }
        message.setText(msg);
        return toastStart;
    }
}
