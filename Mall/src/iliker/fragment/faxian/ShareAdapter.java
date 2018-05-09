package iliker.fragment.faxian;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.image.ImageOptions;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import cn.iliker.mall.R;
import iliker.adapter.DefaultAdapter;
import iliker.entity.Share;
import iliker.holder.BaseHolder;
import iliker.utils.FormatDateTo;
import iliker.utils.GeneralUtil;

@SuppressLint("InflateParams")
public class ShareAdapter extends DefaultAdapter<Share> {
    private final Context context;

    public ShareAdapter(Context context, List<Share> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public BaseHolder getHolder() {
        return new ShareHolder(context);
    }

    private class ShareHolder extends BaseHolder<Share> {
        ShareHolder(Context context) {
            super(context);
        }

        TextView shareContent;
        ImageView headimage;
        ImageView shareimage;
        TextView sharetime;
        TextView locationinfo;
        final ImageOptions imageOptions = new ImageOptions.Builder().setLoadingDrawableId(R.drawable.shanchangdefault).build();

        @Override
        public View initViews() {
            View convertView = View.inflate(context, R.layout.item, null);
            headimage = (ImageView) convertView
                    .findViewById(R.id.headImg);
            shareContent = (TextView) convertView
                    .findViewById(R.id.shareContent);
            shareimage = (ImageView) convertView.findViewById(R.id.shareImg);
            sharetime = (TextView) convertView
                    .findViewById(R.id.releaseTime);
            locationinfo = (TextView) convertView
                    .findViewById(R.id.locationinfo);
            return convertView;
        }

        @Override
        public void refreshView(Share share) {
            String sharetime = share.getReleaseTime();
            String paretime = sharetime.substring(0, sharetime.lastIndexOf("."));
            String headimg = share.getHeadImg();
            try {
                this.shareContent.setText(URLDecoder.decode(share.getContent(),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            this.locationinfo.setText(share.getLocation());
            this.sharetime.setText(FormatDateTo.format(FormatDateTo
                    .StrToDate(paretime)));
            String url = GeneralUtil.SHAREPATH + headimg;
            String imgs = share.getPic();
            String[] imgurls;
            if (imgs.contains("#"))
                imgurls = imgs.split("#");
            else
                imgurls = new String[]{imgs};
            String shareimgurl = GeneralUtil.SHAREPATH + imgurls[0];

// 加载网络图片
            bitmapUtils.bind(this.shareimage, shareimgurl, imageOptions);
            bitmapUtils.bind(this.headimage, url);
        }
    }

}
