package iliker.holder;

import android.content.Context;
import android.view.View;

import org.xutils.ImageManager;

import iliker.utils.BitmapHelp;

public abstract class BaseHolder<T> {
    private final View convertView;
    final protected ImageManager bitmapUtils;
    protected final Context context;

    public BaseHolder(Context context) {
        this.context = context;
        this.convertView = initViews();
        this.convertView.setTag(this);
        bitmapUtils = BitmapHelp.getBitmapUtils();
    }

    public View getConvertView() {
        return convertView;
    }

    public abstract View initViews();

    public void setDatas(T datas) {
        refreshView(datas);
    }

    public abstract void refreshView(T datas);
}
