package iliker.fragment.finding;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import cn.iliker.mall.R;

/**
 * Created by WDHTC on 2016/5/16.
 */
public class InShowPersonAlity extends ShowPersonality {
    private FragmentManager fragmentManager;

    @Override
    protected void initData() {
        fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (listViewFragment == null) listViewFragment = new InListViewFragment();
        listViewFragment.setArguments(bundles);
        ft.replace(R.id.container, listViewFragment).commit();
    }

    @Override
    protected void onResume() {
        SharedPreferences spf = getSharedPreferences("updateSocial", Context.MODE_PRIVATE);
        boolean isupdate = spf.getBoolean("isupdate", false);
        if (isupdate) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            spf.edit().clear().apply();
            ft.remove(listViewFragment).commit();
            listViewFragment = null;
            listViewFragment = new InListViewFragment();
            listViewFragment.setArguments(bundles);
            fragmentManager.beginTransaction().add(R.id.container, listViewFragment).commit();
        }
        super.onResume();
    }

    @Override
    protected void editTitle() {
        menuItem.setTitle("编辑");
    }

    @Override
    protected void menuOnClick() {
        Intent intent = new Intent(this, BatchTailorImgActivity.class);
        startActivity(intent);
    }

}
