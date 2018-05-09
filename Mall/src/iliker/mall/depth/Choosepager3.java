package iliker.mall.depth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import cn.iliker.mall.R;


class Choosepager3 extends Basepager implements RadioGroup.OnCheckedChangeListener {
    private View itemview;

    Choosepager3(Activity activity) {
        super(activity);
    }

    private RadioButton rfw1;
    private RadioButton rfw2;
    private RadioButton rfw3;
    private RadioButton rfw4;
    private String textval;

    @Override
    public void initData() {
        title.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t乳房位置\n（选择与我最匹配的乳房位置）");
        if (itemview == null) {
            itemview = View.inflate(activity, R.layout.rfw_layout, null);
            ((RadioGroup) itemview.findViewById(R.id.rfwgrp)).setOnCheckedChangeListener(this);
            rfw1 = (RadioButton) itemview.findViewById(R.id.rfw1);
            rfw2 = (RadioButton) itemview.findViewById(R.id.rfw2);
            rfw3 = (RadioButton) itemview.findViewById(R.id.rfw3);
            rfw4 = (RadioButton) itemview.findViewById(R.id.rfw4);
        }
        contentPanel.removeAllViews();
        contentPanel.addView(itemview);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        SharedPreferences.Editor editer = sf.edit();
        switch (checkedId) {
            case R.id.rfw1:
                textval = rfw1.getText().toString();
                break;
            case R.id.rfw2:
                textval = rfw2.getText().toString();
                break;
            case R.id.rfw3:
                textval = rfw3.getText().toString();
                break;
            case R.id.rfw4:
                textval = rfw4.getText().toString();
                break;
        }
        try {
            JSONObject jsonObject = new JSONObject(sf.getString("result", ""));
            jsonObject.put("rufanwei", textval);
            editer.putString("result", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editer.apply();
    }
}
