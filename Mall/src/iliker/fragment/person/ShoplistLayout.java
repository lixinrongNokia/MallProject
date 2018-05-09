package iliker.fragment.person;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.xutils.ImageManager;

import iliker.utils.GeneralUtil;

public class ShoplistLayout extends LinearLayout {
    private float density;

    public ShoplistLayout(Context context) {
        super(context);
        initdata();
    }

    public ShoplistLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initdata();
    }

    public ShoplistLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initdata();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShoplistLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initdata();
    }

    private void initdata() {
        density = getResources().getDisplayMetrics().density;
        setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT, (int) (112 * density + 0.5f));
        this.setLayoutParams(params);
    }

    public void setData(Context context, String[] imgs, ImageManager bitmapUtils) {
        LinearLayout.LayoutParams params = new LayoutParams(
                (int) (96 * density + 0.5f), (int) (112 * density + 0.5f));
        if (imgs != null) {
            for (int i = 0; i < 3; i++) {
                try {
                    ImageView imageView = new ImageView(context);
                    imageView.setLayoutParams(params);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    bitmapUtils.bind(imageView, GeneralUtil.GOODSPATH + imgs[i]);
                    this.addView(imageView);
                } catch (ArrayIndexOutOfBoundsException e) {
                    break;
                }
            }
        }
    }
}
