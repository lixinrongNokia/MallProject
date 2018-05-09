package iliker.mall.depth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import cn.iliker.mall.R;

class Choosepager4 extends Basepager implements RadioGroup.OnCheckedChangeListener {
    private View itemview;
    private RadioButton rwx1;
    private RadioButton rwx2;
    private RadioButton rwx3;

    Choosepager4(Activity activity) {
        super(activity);
    }

    private String textval = "";

    @Override
    public void initData() {
        title.setText("\t\t\t\t\t\t\t\t\t\t\t乳房特殊外形\n（选择与我最匹配的乳房外形）");
        if (itemview == null) {
            itemview = View.inflate(activity, R.layout.rwx_layout, null);
            ((RadioGroup) itemview.findViewById(R.id.rwxgrp)).setOnCheckedChangeListener(this);
            rwx1 = (RadioButton) itemview.findViewById(R.id.rwx1);
            rwx2 = (RadioButton) itemview.findViewById(R.id.rwx2);
            rwx3 = (RadioButton) itemview.findViewById(R.id.rwx3);
        }
        contentPanel.removeAllViews();
        contentPanel.addView(itemview);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        SharedPreferences.Editor editor = sf.edit();
        switch (checkedId) {
            case R.id.rwx1:
                textval = rwx1.getText().toString();
                break;
            case R.id.rwx2:
                textval = rwx2.getText().toString();
                break;
            case R.id.rwx3:
                textval = rwx3.getText().toString();
                break;
            case R.id.rwx4:
                textval = "";
                break;
        }
        try {
            JSONObject jsonObject = new JSONObject(sf.getString("result", ""));
            jsonObject.put("ruwaixing", textval);
            editor.putString("result", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }
}
