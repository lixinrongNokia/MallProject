package iliker.mall;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iliker.application.CustomApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import cn.iliker.mall.R;
import iliker.entity.UserInfo;
import iliker.utils.GeneralUtil;

import static iliker.utils.BitmapHelp.getBitmapUtils;

class HeaderHolder {
    private final View convertView;
    private final MainActivity activity;

    HeaderHolder(Context context) {
        this.activity = (MainActivity) context;
        this.convertView = initViews();
        this.convertView.setTag(this);
    }

    public View getConvertView() {
        return convertView;
    }

    private View initViews() {
        View view = View.inflate(activity, R.layout.nav_item, null);
        ImageView headimg = (ImageView) view.findViewById(R.id.head_img);
        TextView signature = (TextView) view.findViewById(R.id.signature);
        UserInfo info = CustomApplication.customApplication.getUserinfo();
        if (info != null) {
            if (!TextUtils.isEmpty(info.getHeadImg()))
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(new File(CustomApplication.cacheDir, info.getHeadImg())));
                    headimg.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    getBitmapUtils().bind(headimg, GeneralUtil.SHAREPATH + info.getHeadImg());
                }
            if (!TextUtils.isEmpty(info.getSignature()))
                signature.setText(info.getSignature());
        }
        return view;
    }

}
