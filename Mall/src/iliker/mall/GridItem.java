package iliker.mall;

/**
 * @author Kiritor
 * 实现自己的View继承Checable接口
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.iliker.mall.R;

public class GridItem extends RelativeLayout implements Checkable {

    private boolean mChecked;// 判断该选项是否被选上的标志量
    private ImageView mImgView = null;
    private ImageView mSecletView = null;
    private TextView objname = null;
    private String text;

    public GridItem(Context context) {
        this(context, null, 0);
    }

    public GridItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.grid_item, this);
        mImgView = (ImageView) findViewById(R.id.img_view);
        mSecletView = (ImageView) findViewById(R.id.select);
        objname = (TextView) findViewById(R.id.objname);
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        mSecletView.setVisibility(checked ? View.VISIBLE : View.GONE);// 选上了则显示小勾图片
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    public void setImgResId(int resId) {
        if (mImgView != null) {
            mImgView.setImageResource(resId);
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setValToName() {
        if (objname != null)
            objname.setText(this.text);
    }

}
