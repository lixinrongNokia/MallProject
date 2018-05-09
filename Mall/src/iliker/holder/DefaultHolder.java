package iliker.holder;

import android.content.Context;
import android.view.View;
public abstract class DefaultHolder {
    private final View convertView;
    protected final Context context;

    protected DefaultHolder(Context context) {
        this.context = context;
        this.convertView = initViews();
        this.convertView.setTag(this);
    }

    public View getConvertView() {
        return convertView;
    }

    protected abstract View initViews();

}
