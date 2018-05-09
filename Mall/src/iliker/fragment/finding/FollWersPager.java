package iliker.fragment.finding;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iliker.application.CustomApplication;

import java.util.List;
import java.util.Map;

import cn.iliker.mall.R;
import iliker.entity.UserInfo;
import iliker.utils.ParsJsonUtil;

public abstract class FollWersPager extends Fragment implements
        OnItemClickListener {
    private ListView folliv;
    TextView empty;
    private View view;
    private Context context;
    private CustomApplication cap;
    private UserInfo userInfo;
    String state;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.follwers, null);
            findViews();
            setListener();
        }
        initProperty();
        initDate();
        return view;
    }

    protected abstract void initProperty();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        cap = (CustomApplication) context.getApplicationContext();
        userInfo = cap.getUserinfo();
    }

    private void initDate() {
        if (!cap.networkIsAvailable()) {
            Toast.makeText(context, "没网络", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userInfo == null) {
            folliv.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            return;
        }
        new AsyncGetFoll().execute(cap.getUname());
    }


    private void setListener() {
        folliv.setOnItemClickListener(this);
    }

    private void findViews() {
        folliv = (ListView) view.findViewById(R.id.folliv);
        empty = (TextView) view.findViewById(R.id.empty);
    }

    private class AsyncGetFoll extends
            AsyncTask<String, String, List<Map<String, String>>> {

        @Override
        protected List<Map<String, String>> doInBackground(String... params) {
            return ParsJsonUtil.getFolle(state, params[0]);
        }

        @Override
        protected void onPostExecute(List<Map<String, String>> result) {
            if (result != null) {
                empty.setVisibility(View.GONE);
                folliv.setVisibility(View.VISIBLE);
                folliv.setAdapter(new FollWerAdapter(state, context, result));
            } else {
                folliv.setVisibility(View.GONE);
                empty.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    }
}
