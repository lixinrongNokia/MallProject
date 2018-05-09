package iliker.fragment.mystore;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import cn.iliker.mall.R;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class PartnersActivity extends BaseStoreActivity implements View.OnClickListener{
private LinearLayout webdownload;
private LinearLayout qrCodeShare;
private LinearLayout proxy;
    private View view;
    @Override
    protected void initMyViews() {
        title.setText("邀请合伙人");
         view=View.inflate(this,R.layout.invitepartners_layout,null);
        storeContent.addView(view);
        findChildViews();
        setChildListener();
    }

    private void findChildViews() {
        webdownload= (LinearLayout) view.findViewById(R.id.webdownload);
        qrCodeShare= (LinearLayout) view.findViewById(R.id.qrCodeShare);
        proxy= (LinearLayout) view.findViewById(R.id.proxy);
    }

    private void setChildListener() {
        webdownload.setOnClickListener(this);
        qrCodeShare.setOnClickListener(this);
        proxy.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.webdownload:
                share2Other("http://iliker.cn/wx/index.jsp?superiornum=18680602795","http://iliker.cn/wx/index.jsp?superiornum=18680602795","http://iliker.cn/img/logo.png");
                break;
            case R.id.qrCodeShare:
                share2Other("http://iliker.cn/download.html","http://iliker.cn/download.html","http://iliker.cn/img/download.png");
                break;
            case R.id.proxy:
                Intent intent = new Intent(this, PromoteActivity.class);
                intent.putExtra("goldtwitter",true);
                startActivity(intent);
                break;
        }
    }

    private void share2Other(String content,String titleUrl,String imgUrl){
        OnekeyShare oks = new OnekeyShare();
        // 关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setTitle("好友邀请");
        oks.setTitleUrl(content);
        oks.setText("点击链接空间打开的");
        oks.setImageUrl(imgUrl);
        oks.setUrl(content);
        oks.setComment("打开链接http://iliker.cn/");
        oks.setSite("iliker");
        oks.setSiteUrl(content);
        // 启动分享GUI
        oks.show(this);
    }
}
