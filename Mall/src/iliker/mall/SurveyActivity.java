package iliker.mall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.iliker.application.CustomApplication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.iliker.mall.R;
import iliker.mall.depth.DepthActivity;

/**
 * 身形预测
 *
 * @author Administrator
 */
public class SurveyActivity extends BaseActivity implements
        OnSeekBarChangeListener, OnClickListener {
    private String mCurrentCupName;
    private double height = 150;// 身高值
    private double onderbust;//上胸围值
    private double underbust;//下胸围值
    private double waist;//腰围值
    private double hip;//臀围值
    private double weight;//体重
    private double bmi;//体重身高比
    private String underpants = "";//内裤尺寸
    private String typeName = "";

    private NumberSeekBar pb;
    private Button submitbtn;
    private List<TextView> list = new ArrayList<>();
    private List<TextView> list2 = new ArrayList<>();
    private List<TextView> list3 = new ArrayList<>();
    private Map<Integer, TextView> maps = new HashMap<>();
    private Map<Integer, TextView> maps2 = new HashMap<>();
    private Map<Integer, TextView> maps3 = new HashMap<>();
    private final MyListener myListener = new MyListener(maps3);
    private final MyListener myListener2 = new MyListener(maps2);
    private PopupWindow popupWindow;
    private ResultObj obj = null;
    private AlertDialog.Builder alertbuilder;
    private String mNames[] = {"红", "橙", "紫", "青", "靛 ", "蓝", "白", "黑", "肤色",
            "少女之心", "经典之美", "时尚之都"};
    private String mNames2[] = {"纤细瘦弱", "标准适中", "圆润丰满", "凸凹波霸"};
    private String mNames3[] = {"20+", "30+", "40+", "50+"};
    private XCFlowLayout flowlayout;
    private XCFlowLayout flowlayout2;
    private XCFlowLayout flowlayout3;
    private final StringBuffer builder = new StringBuffer();
    private View view = null;
    private int statusBarHeight;
    private AlertDialog dlg;

    @Override
    protected void subClassInit() {
        setContentView(R.layout.selecttags);
        findViews();
        initChildViews();
        init();
        setListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        createView();
        createpopupWindow();
    }

    private void createpopupWindow() {
        if (popupWindow == null) {
            Rect frame = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            statusBarHeight = frame.top;
            popupWindow = new PopupWindow(this);

            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setWidth(getWindowManager().getDefaultDisplay()
                    .getWidth());
            popupWindow.setHeight(getWindowManager().getDefaultDisplay()
                    .getHeight() - statusBarHeight);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setFocusable(true);
            popupWindow.setAnimationStyle(R.style.animationfade); // 设置动画
            popupWindow.setContentView(view);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popupWindow.setAnimationStyle(R.style.animationfade); // 设置动画
                }

            });
        }
    }

    private void findViews() {
        pb = (NumberSeekBar) this.findViewById(R.id.mybar);
        flowlayout = (XCFlowLayout) findViewById(R.id.flowlayout);
        flowlayout2 = (XCFlowLayout) findViewById(R.id.flowlayout2);
        flowlayout3 = (XCFlowLayout) findViewById(R.id.flowlayout3);
        submitbtn = (Button) findViewById(R.id.submitbtn);
    }

    private void setListener() {
        submitbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (maps2.isEmpty()) {
                    Toast.makeText(SurveyActivity.this, "选择一项身型", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (maps3.isEmpty()) {
                    Toast.makeText(SurveyActivity.this, "选择一项年龄", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (maps.size() > 0) {
                    for (Map.Entry<Integer, TextView> entry : maps.entrySet()) {
                        builder.append(entry.getValue().getText()).append(",");
                    }
                    builder.deleteCharAt(builder.length() - 1);
                }
                calculate();
                saveData();
                getCupName(onderbust, underbust);
                obj.shengao.setText(String.valueOf((int) height));
                obj.xiongwei.setText(String.valueOf((int) onderbust));
                obj.xiongwei2.setText(String.valueOf((int) (underbust + 0.5)));
                obj.yaowei.setText(String.valueOf((int) waist));
                obj.tunwei.setText(String.valueOf((int) hip));
                obj.tizhong.setText(String.valueOf((int) weight));
                obj.tixing.setText(typeName);
                obj.bmi.setText(String.valueOf(bmi));
                obj.cuptype.setText(mCurrentCupName);
                obj.tags.setText(builder.toString());
                obj.dikusize.setText(underpants);
                getSharedPreferences("result", Context.MODE_PRIVATE).edit()
                        .putString("cuptype", mCurrentCupName).putString("underbust", String.valueOf((int) (underbust + 0.5)))
                        .apply();
                popupWindow.showAtLocation(findViewById(R.id.ViewId), Gravity.START
                        | Gravity.CENTER, 0, statusBarHeight);// 需要指定Gravity，默认情况是center.
            }

        });
        pb.setOnSeekBarChangeListener(this);
        for (TextView view : list) {
            view.setOnClickListener(this);
        }

        for (TextView view : list2) {
            view.setOnClickListener(myListener2);
        }
        for (TextView view : list3) {
            view.setOnClickListener(myListener);
        }

    }

    class MyListener implements OnClickListener {
        private final Map<Integer, TextView> map;

        MyListener(Map<Integer, TextView> map) {
            this.map = map;
        }

        @Override
        public void onClick(View v) {
            TextView vi = (TextView) v;
            if (map.get(vi.getId()) == null) {
                if (!map.isEmpty()) {
                    Iterator<Entry<Integer, TextView>> it = map
                            .entrySet().iterator();
                    while (it.hasNext()) {
                        TextView t = it.next().getValue();
                        t.setTextColor(getResources().getColor(
                                R.color.myblack));
                        t.setBackgroundResource(R.drawable.sel_textview_bg);
                        it.remove();
                    }

                }
                map.put(vi.getId(), vi);
                vi.setTextColor(getResources().getColor(
                        R.color.defaultWhite));
                vi.setBackgroundResource(R.drawable.no_textview_bg);

            } else {
                map.clear();
                vi.setTextColor(getResources()
                        .getColor(R.color.myblack));
                vi.setBackgroundResource(R.drawable.sel_textview_bg);
            }
        }
    }

    private void initChildViews() {
        MarginLayoutParams lp = new MarginLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        lp.leftMargin = 10;
        lp.rightMargin = 10;
        lp.topMargin = 10;
        lp.bottomMargin = 10;
        for (int i = 0; i < mNames.length; i++) {
            TextView view = new TextView(this);
            view.setId(i + 1);
            list.add(view);
            view.setText(mNames[i]);
            view.setTextColor(getResources().getColor(R.color.myblack));
            view.setBackgroundResource(R.drawable.sel_textview_bg);
            flowlayout.addView(view, lp);
        }
        for (int i = 0; i < mNames2.length; i++) {
            TextView view = new TextView(this);
            view.setId(i + 20);
            list2.add(view);
            view.setText(mNames2[i]);
            view.setTextColor(getResources().getColor(R.color.myblack));
            view.setBackgroundResource(R.drawable.sel_textview_bg);
            flowlayout2.addView(view, lp);
        }
        for (int i = 0; i < mNames3.length; i++) {
            TextView view = new TextView(this);
            view.setId(i + 20);
            list3.add(view);
            view.setText(mNames3[i]);
            view.setTextColor(getResources().getColor(R.color.myblack));
            view.setBackgroundResource(R.drawable.sel_textview_bg);
            flowlayout3.addView(view, lp);
        }
    }

    private void init() {
        pb.setTextSize(36);// 设置字体大小
        pb.setTextColor(Color.WHITE);// 颜色
        pb.setMyPadding(10, 10, 10, 10);// 设置padding 调用setpadding会无效
    }

    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        height = arg1 + 150;
        pb.setProgress(arg1);
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
    }

    @Override
    protected void onPause() {
        if (dlg != null) dlg.dismiss();
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        TextView vi = (TextView) v;
        if (maps.get(vi.getId()) == null) {
            maps.put(vi.getId(), vi);
            vi.setTextColor(getResources().getColor(R.color.defaultWhite));
            vi.setBackgroundResource(R.drawable.no_textview_bg);
        } else {
            maps.remove(vi.getId());
            vi.setTextColor(getResources().getColor(R.color.myblack));
            vi.setBackgroundResource(R.drawable.sel_textview_bg);
        }
    }

    private void calculate() {
        onderbust = height * 0.5235;
        underbust = height * 0.457;
        waist = height * 0.382;
        hip = height * 0.542;
        weight = (height - 100) * 0.9 - 2.5;
        Double dfbmi = weight / (Math.pow(height / 100, 2));
        BigDecimal bigbmi = new BigDecimal(dfbmi);
        bmi = bigbmi.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private void saveData() {
        int t1 = (int) (hip + 0.5);
        if (t1 >= 80 && t1 < 85) {
            underpants = "S";
        } else if (t1 >= 85 && t1 < 90) {
            underpants = "M";
        } else if (t1 >= 90 && t1 < 95) {
            underpants = "L";
        } else if (t1 >= 95 && t1 < 100) {
            underpants = "XL";
        } else {
            underpants = "XXL";
        }
        getType(height, waist);
        Editor editor = getSharedPreferences("result", MODE_PRIVATE).edit();
        editor.putString("height", String.valueOf((int) (height + 0.5)));
        editor.putString("onderbust", String.valueOf((int) (onderbust + 0.5)));
        editor.putString("waist", String.valueOf((int) (waist + 0.5)));
        editor.putString("hip", t1 + "");
        editor.putString("weight", (int) weight + "");
        editor.putString("typetext", typeName);
        editor.putString("bmi", bmi + "");
        editor.putString("tags", builder.toString());
        editor.putString("underpants", underpants);
        editor.apply();
        editor = getSharedPreferences("sncremind", MODE_PRIVATE).edit();
        editor.putInt("snc", 1);
        editor.apply();
    }

    // 处理后退键的情况
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            overridePendingTransition(R.anim.back_left_in,
                    R.anim.back_right_out);
            this.finish(); // finish当前activity
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void createView() {
        if (view == null) {
            view = getLayoutInflater().inflate(R.layout.confirm, null);
            obj = new ResultObj();
            obj.shengao = (TextView) view.findViewById(R.id.shengao);
            obj.xiongwei = (TextView) view.findViewById(R.id.xiongwei);
            obj.xiongwei2 = (TextView) view.findViewById(R.id.xiongwei2);
            obj.yaowei = (TextView) view.findViewById(R.id.yaowei);
            obj.tunwei = (TextView) view.findViewById(R.id.tunwei);
            obj.tizhong = (TextView) view.findViewById(R.id.tizhong);
            obj.tixing = (TextView) view.findViewById(R.id.tixing);
            obj.nosave = (Button) view.findViewById(R.id.nosave);
            obj.save = (Button) view.findViewById(R.id.save);
            obj.determine = (Button) view.findViewById(R.id.determine);
            obj.depth = (Button) view.findViewById(R.id.depth);
            obj.bmi = (TextView) view.findViewById(R.id.bmi);
            obj.cuptype = (TextView) view.findViewById(R.id.cuptype);
            obj.tags = (TextView) view.findViewById(R.id.tags);
            obj.dikusize = (TextView) view.findViewById(R.id.dikusize);
            obj.nologin = (RelativeLayout) view.findViewById(R.id.nologin);
        }
        obj.nosave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertbuilder = new AlertDialog.Builder(SurveyActivity.this);
                alertbuilder.setIcon(R.drawable.logo);
                alertbuilder.setTitle("提醒:");
                alertbuilder.setMessage("您尚未加入爱内秀，您的私密数据不会被同步！");
                alertbuilder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Intent intent = new Intent();
                                intent.setClass(SurveyActivity.this,
                                        MainActivity.class);
                                startActivity(intent);
                                for (Activity act : CustomApplication.actlist) {
                                    act.finish();
                                }
                                CustomApplication.actlist.clear();
                            }

                        }).setNegativeButton("取消", null).show();
            }

        });
        obj.depth.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                createAlertView();
            }

        });
        obj.save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SurveyActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
            }

        });

        obj.determine.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(SurveyActivity.this, MainActivity.class);
                startActivity(intent);
                for (Activity act : CustomApplication.actlist) {
                    act.finish();
                }
                CustomApplication.actlist.clear();
            }
        });
        if (cap.getUserinfo() == null) {
            obj.nologin.setVisibility(View.VISIBLE);
            obj.determine.setVisibility(View.GONE);
        } else {
            obj.nologin.setVisibility(View.GONE);
            obj.determine.setVisibility(View.VISIBLE);
        }
    }

    private void createAlertView() {
        if (dlg == null)
            dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        if (window != null) {
            window.setContentView(R.layout.alertpop_layout);
            Button button = (Button) window.findViewById(R.id.forward);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(SurveyActivity.this, DepthActivity.class);
                    startActivity(intent);
                }
            });
            Button button1 = (Button) window.findViewById(R.id.cancel);
            button1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.dismiss();
                }
            });
        }
    }

    class ResultObj {
        private TextView shengao, xiongwei, xiongwei2, yaowei, tunwei, tizhong,
                tixing, bmi, tags, cuptype, dikusize;
        private Button nosave, save, determine, depth;
        private RelativeLayout nologin;
    }

    private void getType(double height, double waist) {
        BigDecimal h = new BigDecimal(height);
        BigDecimal w = new BigDecimal(waist);

        double bh = w.divide(h, 3, BigDecimal.ROUND_HALF_EVEN).doubleValue();
        if (bh > 0.575 && bh <= 0.595) {
            typeName = "梨子型";
        } else if (bh > 0.525 && bh <= 0.575) {
            typeName = "甘蔗型";
        } else if (bh > 0.355 && bh <= 0.495) {
            typeName = "草莓型";
        } else if (bh >= 0.495 && bh <= 0.525) {
            typeName = "葫芦型";
        } else if (bh > 0.595) typeName = "苹果型";
    }

    private void getCupName(double b, double bd) {
        double val = b - bd;
        switch ((int) (bd + 0.5)) {
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
                if (val >= 10.0 && val < 12.5) {
                    mCurrentCupName = "70A/32A";
                } else if (val >= 12.5 && val < 15) {
                    mCurrentCupName = "70B/32B";
                } else if (val >= 15 && val < 17.5) {
                    mCurrentCupName = "70c/32c";
                } else if (val >= 17.5 && val < 20) {
                    mCurrentCupName = "70D/32D";
                } else if (val >= 20) {
                    mCurrentCupName = "70E/32E";
                }
                break;
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
                if (val >= 10.0 && val < 12.5) {
                    mCurrentCupName = "75A/34A";
                } else if (val >= 12.5 && val < 15) {
                    mCurrentCupName = "75B/34B";
                } else if (val >= 15 && val < 17.5) {
                    mCurrentCupName = "75c/34c";
                } else if (val >= 17.5 && val < 20) {
                    mCurrentCupName = "75D/34D";
                } else if (val >= 20) {
                    mCurrentCupName = "75E/34E";
                }
                break;
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
                if (val >= 10.0 && val < 12.5) {
                    mCurrentCupName = "80A/36A";
                } else if (val >= 12.5 && val < 15) {
                    mCurrentCupName = "80B/36B";
                } else if (val >= 15 && val < 17.5) {
                    mCurrentCupName = "80c/36c";
                } else if (val >= 17.5 && val < 20) {
                    mCurrentCupName = "80D/36D";
                } else if (val >= 20) {
                    mCurrentCupName = "80E/36E";
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        list.clear();
        list = null;
        list2.clear();
        list2 = null;
        list3.clear();
        list3 = null;
        mNames = null;
        mNames2 = null;
        mNames3 = null;
        maps.clear();
        maps = null;
        maps2.clear();
        maps2 = null;
        maps3.clear();
        maps3 = null;
        super.onDestroy();
    }

}
