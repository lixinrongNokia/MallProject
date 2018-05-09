package com.fjl.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import cn.iliker.mall.R;

public final class DialogFactory {

    private DialogFactory() {
    }

    public static Dialog initDialog(Context context) {
        Dialog progressDialog = new Dialog(context,
                R.style.like_toast_dialog_style);
        progressDialog.setContentView(R.layout.btncomm_dialog);
        Window window = progressDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        progressDialog.setCancelable(false);
        return progressDialog;
    }

}
