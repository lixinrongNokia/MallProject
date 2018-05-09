package iliker.mall;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.iliker.mall.R;

import java.util.ArrayList;
import java.util.List;

import static com.iliker.application.CustomApplication.exitSystem;

public class SelectSexActivity extends BaseActivity implements OnTouchListener {
    private ImageView iv_male, iv_female;
    private String sex = "女";
    private List<ImageView> list = null;
    private GestureDetector mGestureDetector;
    private LinearLayout viewSnsLayout;
    private final int verticalMinDistance = 20;
    private final int minVelocity = 0;
    private long exitTime = 0;

    @Override
    protected void subClassInit() {
        setContentView(R.layout.select_sex);
        findViews();
        mGestureDetector = new GestureDetector(this, new SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > verticalMinDistance
                        && Math.abs(velocityX) > minVelocity) {
                    getSharedPreferences("sex", Context.MODE_PRIVATE).edit()
                            .putString("sex", sex).apply();
                    // 切换Activity
                    if ("男".equals(sex)) {
                        Toast.makeText(SelectSexActivity.this, "男生版没有构建", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Intent intent = new Intent(SelectSexActivity.this,
                            SurveyActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
        viewSnsLayout.setOnTouchListener(this);
        viewSnsLayout.setLongClickable(true);

        setListener();
        setSelection(sex);
    }

    private void findViews() {
        viewSnsLayout = (LinearLayout) findViewById(R.id.ll_footer);
        list = new ArrayList<>();
        iv_male = (ImageView) findViewById(R.id.iv_male);
        iv_female = (ImageView) findViewById(R.id.iv_female);
        list.add(iv_male);
        list.add(iv_female);
    }

    private void setListener() {
        for (ImageView iv : list) {
            iv.setOnTouchListener(this);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent arg1) {
        switch (v.getId()) {
            case R.id.iv_male:
                if ("男".equals(sex)) {
                    return false;
                }
                sex = "男";
                break;
            case R.id.iv_female:
                if ("女".equals(sex)) {
                    return false;
                }
                sex = "女";
                break;
            default:
                return mGestureDetector.onTouchEvent(arg1);
        }
        setSelection(sex);
        return true;
    }

    private void setSelection(String sex) {
        switch (sex) {
            case "男":
                iv_male.setImageBitmap(parsBitmap(getResources(),
                        R.drawable.select_book_selected_boy));
                iv_female.setImageBitmap(parsBitmap(getResources(),
                        R.drawable.select_book_nor_girl));
                break;

            case "女":
                iv_male.setImageBitmap(parsBitmap(getResources(),
                        R.drawable.select_book_nor_boy));
                iv_female.setImageBitmap(parsBitmap(getResources(),
                        R.drawable.select_book_selected_girl));
                break;

        }
    }

    private static Bitmap parsBitmap(Resources res, int resid) {
        return BitmapFactory.decodeResource(res, resid);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev); // 让Activity响应触碰事件
        mGestureDetector.onTouchEvent(ev); // 让GestureDetector响应触碰事件
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (getIntent().getBooleanExtra("istwos", false)) {
                Toast.makeText(getApplicationContext(), "现在不能退出!",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            exitSystem();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
