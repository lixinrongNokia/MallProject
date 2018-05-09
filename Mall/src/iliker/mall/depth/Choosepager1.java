package iliker.mall.depth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import cn.iliker.mall.R;

public class Choosepager1 extends Basepager implements RadioGroup.OnCheckedChangeListener {
    private View itemview;

    public Choosepager1(Activity activity) {
        super(activity);
    }

    private RadioButton rj1, rj2, rj3, rj4, rj5;
    private CharSequence textval;

    @Override
    public void initData() {
        title.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t乳间距\n(选择与我最匹配的乳房间距类型)");
        if (itemview == null) {
            itemview = View.inflate(activity, R.layout.rj_layout, null);
            ((RadioGroup) itemview.findViewById(R.id.rjgrp)).setOnCheckedChangeListener(this);
            rj1 = (RadioButton) itemview.findViewById(R.id.rj1);
            rj2 = (RadioButton) itemview.findViewById(R.id.rj2);
            rj3 = (RadioButton) itemview.findViewById(R.id.rj3);
            rj4 = (RadioButton) itemview.findViewById(R.id.rj4);
            rj5 = (RadioButton) itemview.findViewById(R.id.rj5);
        }
        contentPanel.removeAllViews();
        contentPanel.addView(itemview);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        SharedPreferences.Editor editer = sf.edit();
        switch (checkedId) {
            case R.id.rj1:
                textval = rj1.getText();
                break;
            case R.id.rj2:
                textval = rj2.getText();
                break;
            case R.id.rj3:
                textval = rj3.getText();
                break;
            case R.id.rj4:
                textval = rj4.getText();
                break;
            case R.id.rj5:
                textval = rj5.getText();
                break;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rujianju", textval);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editer.putString("result", jsonObject.toString());
        editer.apply();
    }
}

