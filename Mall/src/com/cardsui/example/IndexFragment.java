package com.cardsui.example;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import cn.iliker.mall.R;
import com.cardsui.example.goods.ClassificationActivity;
import com.cardsui.example.goods.FlashSaleActivity;
import com.cardsui.example.goods.NewProductActivity;
import com.cardsui.example.goods.NewProductHolder;
import com.fjl.widget.ToastFactory;
import countdowntimer.CountDownTimerView;
import iliker.fragment.BaseFragment;
import iliker.fragment.type.SearchActivity;
import org.xing.qr_code.scan.MipcaActivityCapture;

import java.util.Calendar;

import static iliker.utils.CheckUtil.isUrlLink;
import static iliker.utils.FormatDateTo.format;

public class IndexFragment extends BaseFragment implements OnClickListener, CountDownTimerView.OnTimeToListener {
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private RelativeLayout rl;
    private ScrollView scrollView;
    private ImageView qrcode;
    private TextView try_, free, bargaining, classify_index;// 新品尝鲜，零元抢购，超级砍杀，商品分类
    private RelativeLayout anchor1, anchor2;
    private CountDownTimerView countdowntimerview;
    private TextView newsalemore;//查看更多新品，抢购
    private FrameLayout NewProductFragment;
    private NewProductHolder newProductHolder;
    private View view;


    @Override
    protected View initSubclassView() {
        view = View.inflate(context, R.layout.index, null);
        findViews();
        setListener();
        if (newProductHolder == null) {
            newProductHolder = new NewProductHolder(context);
        }
        NewProductFragment.removeAllViews();
        NewProductFragment.addView(newProductHolder.getConvertView());
        return view;
    }

    private void initData() {
        long interval = format() - Calendar.getInstance().getTimeInMillis();
        countdowntimerview.setTime((int) (interval / 1000));
        countdowntimerview.startCountDown();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    private void findViews() {
        rl = (RelativeLayout) view.findViewById(R.id.link_search);
        qrcode = (ImageView) view.findViewById(R.id.qrcode);
        try_ = (TextView) view.findViewById(R.id.try_);
        free = (TextView) view.findViewById(R.id.free);
        bargaining = (TextView) view.findViewById(R.id.bargaining);
        classify_index = (TextView) view.findViewById(R.id.classify_index);
        anchor1 = (RelativeLayout) view.findViewById(R.id.anchor1);
        anchor2 = (RelativeLayout) view.findViewById(R.id.anchor2);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        countdowntimerview = (CountDownTimerView) view.findViewById(R.id.countdowntimerview);
        newsalemore = (TextView) view.findViewById(R.id.newsalemore);
        NewProductFragment = (FrameLayout) view.findViewById(R.id.NewProductFragment);
    }

    private void setListener() {
        rl.setOnClickListener(this);
        qrcode.setOnClickListener(this);
        try_.setOnClickListener(this);
        free.setOnClickListener(this);
        bargaining.setOnClickListener(this);
        classify_index.setOnClickListener(this);
        countdowntimerview.setOnTimeToListener(this);
        anchor2.setOnClickListener(this);
        newsalemore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.link_search:
                Intent intent = new Intent(context, SearchActivity.class);
                context.startActivity(intent);
                break;
            case R.id.qrcode:
                int asPermission = ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA);
                if (asPermission == PackageManager.PERMISSION_GRANTED) {
                    Intent intent1 = new Intent();
                    intent1.setClass(context, MipcaActivityCapture.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent1, SCANNIN_GREQUEST_CODE);
                } else ToastFactory.getMyToast("没有获得相机权限").show();
                break;
            case R.id.try_:
                scrollView.smoothScrollTo(0, anchor1.getTop());
                break;
            case R.id.free:
                scrollView.smoothScrollTo(0, anchor2.getTop());
                break;
            case R.id.bargaining:
                Intent intent2 = new Intent(context, BargainingActivity.class);
                startActivity(intent2);
                break;
            case R.id.classify_index:
                Intent classintent = new Intent(context, ClassificationActivity.class);
                startActivity(classintent);
                break;
            case R.id.anchor2:
                Intent intent3 = new Intent(context, FlashSaleActivity.class);
                startActivity(intent3);
                break;
            case R.id.newsalemore:
                Intent intent4 = new Intent(context, NewProductActivity.class);
                startActivity(intent4);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            String result = data.getStringExtra("result");
            if (isUrlLink(result)) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                if (!result.contains("//")) {
                    result = "http://" + result;
                }
                intent.setData(Uri.parse(result));
                startActivity(intent);
            } else
                ToastFactory.getMyToast(result).show();
        }
    }

    @Override
    public void handleTimeToEvent() {
        countdowntimerview.stopCountDown();
        initData();
    }

    @Override
    public void onPause() {
        super.onPause();
        countdowntimerview.stopCountDown();
    }
}
