package iliker.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static iliker.utils.ViewUtils.removeParent;

public abstract class BaseFragment extends Fragment {
    protected Activity context;
    protected View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null)
            view = initSubclassView();
        removeParent(view);
        return view;
    }

    protected abstract View initSubclassView();

    @Override
    public void onAttach(Activity context) {
        this.context = context;
        super.onAttach(context);
    }
}
