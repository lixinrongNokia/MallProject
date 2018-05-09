package iliker.fragment.finding;

import android.content.Context;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class BaseFindPager {
    final Context context;
    public View view;
    final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);

    BaseFindPager(Context context) {
        this.context = context;
        view = initView();
    }

    protected abstract View initView();

    public abstract void initDate();
}
