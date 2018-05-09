package iliker.mall.depth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import cn.iliker.mall.R;
import iliker.entity.DepthResult;
import iliker.mall.BaseActivity;

@ContentView(R.layout.result_layout)
public class Result_Activity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.resultlist)
    private LinearLayout resultlist;
    private DepthResult depthResult;
    private final Map<String, TextView> map = new HashMap<>();

    @Override
    protected void subClassInit() {
        initdatas();
        setListener();
    }

    private void setListener() {
        if (!map.isEmpty()) {
            for (Map.Entry<String, TextView> text : map.entrySet()) {
                TextView t = text.getValue();
                t.setOnClickListener(this);
            }
        }
    }

    private void initdatas() {
        SharedPreferences sf = getSharedPreferences("depth_result", Context.MODE_PRIVATE);
        String result = sf.getString("result", "");
        if (!TextUtils.isEmpty(result)) {
            depthResult = JSON.parseObject(result, DepthResult.class);
        }
        if (depthResult != null) {
            float density = getResources().getDisplayMetrics().density;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);  // , 1是可选写的
            lp.setMargins(0, (int) (5 * density), 0, 0);
            Drawable drawable = getResources().getDrawable(R.drawable.mark);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
            Field[] field = depthResult.getClass().getDeclaredFields();        //获取实体类的所有属性，返回Field数组
            for (Field field1 : field) {     //遍历所有属性
                String name = field1.getName();    //获取属性的名字
                name = name.substring(0, 1).toUpperCase() + name.substring(1); //将属性的首字符大写，方便构造get，set方法
                String type = field1.getGenericType().toString();    //获取属性的类型
                if (type.equals("class java.lang.String")) {   //如果type是类类型，则前面包含"class "，后面跟类名
                    try {
                        Method m = depthResult.getClass().getMethod("get" + name);
                        String value = (String) m.invoke(depthResult);    //调用getter方法获取属性值
                        if (value != null) {
                            TextView text1 = new TextView(this);
                            text1.setTag(name);
                            text1.setGravity(Gravity.CENTER);
                            text1.setLayoutParams(lp);
                            text1.setCompoundDrawables(drawable, null, null, null);
                            text1.setCompoundDrawablePadding((int) (10 * density + 0.5f));
                            text1.setText(Html.fromHtml("<u>" + value + "</u>"));
                            resultlist.addView(text1);
                            map.put(name, text1);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        TextView tv = (TextView) v;
        String pamars = tv.getText().toString();
        Intent intent = new Intent(this, MatchProductActivity.class);
        intent.putExtra("matchval", pamars);
        startActivity(intent);
    }
}
