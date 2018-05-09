package iliker.mall.depth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import cn.iliker.mall.R;

/**
 * Created by WDHTC on 2016/3/16.
 */
public class Choosepager5 extends Basepager implements RadioGroup.OnCheckedChangeListener {
    private View itemview;
    private RadioButton rtf1;
    private RadioButton rtf2;
    private RadioButton rtf3;
    private String textval;

    public Choosepager5(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        title.setText("\t\t\t\t\t\t\t\t乳头方向\n（选择与我最匹配的乳头方向）");
        if (itemview == null) {
            itemview = View.inflate(activity, R.layout.rtf_layout, null);
            ((RadioGroup) itemview.findViewById(R.id.rtfgrp)).setOnCheckedChangeListener(this);
            rtf1 = (RadioButton) itemview.findViewById(R.id.rtf1);
            rtf2 = (RadioButton) itemview.findViewById(R.id.rtf2);
            rtf3 = (RadioButton) itemview.findViewById(R.id.rtf3);
        }
        contentPanel.removeAllViews();
        contentPanel.addView(itemview);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        SharedPreferences.Editor editor = sf.edit();
        switch (checkedId) {
            case R.id.rtf1:
                textval = rtf1.getText().toString();
                break;
            case R.id.rtf2:
                textval = rtf2.getText().toString();
                break;
            case R.id.rtf3:
                textval = rtf3.getText().toString();
                break;
        }
        try {
            JSONObject jsonObject = new JSONObject(sf.getString("result", ""));
            jsonObject.put("rutoufanxiang", textval);
            editor.putString("result", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }
}
