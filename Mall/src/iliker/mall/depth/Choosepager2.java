package iliker.mall.depth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import cn.iliker.mall.R;

public class Choosepager2 extends Basepager implements RadioGroup.OnCheckedChangeListener {
    private View itemview;
    private RadioButton fm1, fm2, fm3, fm4;

    public Choosepager2(Activity activity) {
        super(activity);
    }

    public View itemView;
    private CharSequence textval;

    @Override
    public void initData() {
        title.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t丰满度\n（选择与我最匹配的乳房丰满度）");
        if (itemview == null) {
            itemview = View.inflate(activity, R.layout.fm_layout, null);
            ((RadioGroup) itemview.findViewById(R.id.fmgrp)).setOnCheckedChangeListener(this);
            fm1 = (RadioButton) itemview.findViewById(R.id.fm1);
            fm2 = (RadioButton) itemview.findViewById(R.id.fm2);
            fm3 = (RadioButton) itemview.findViewById(R.id.fm3);
            fm4 = (RadioButton) itemview.findViewById(R.id.fm4);
        }
        contentPanel.removeAllViews();
        contentPanel.addView(itemview);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        SharedPreferences.Editor editer = sf.edit();
        switch (checkedId) {
            case R.id.fm1:
                textval = fm1.getText().toString();
                break;
            case R.id.fm2:
                textval = fm2.getText().toString();
                break;
            case R.id.fm3:
                textval = fm3.getText().toString();
                break;
            case R.id.fm4:
                textval = fm4.getText().toString();
                break;
        }
        try {
            JSONObject jsonObject = new JSONObject(sf.getString("result", ""));
            jsonObject.put("fenmandu", textval);
            editer.putString("result", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editer.apply();
    }
}
