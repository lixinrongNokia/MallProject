package iliker.mall;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import cn.iliker.mall.storemodule.Store_MainActivity;
import com.iliker.application.CustomApplication;
import iliker.entity.StoreInfo;
import iliker.entity.UserInfo;
import iliker.utils.ViewUtils;

import java.util.Timer;
import java.util.TimerTask;

import static com.iliker.application.CustomApplication.openIMLogin;

/**
 * app启动页面
 *
 * @author Administrator
 */
public class StartActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                int switchID = getSharedPreferences("switch_GUI", Context.MODE_PRIVATE).getInt("switchID", 1);
                switch (switchID) {
                    case 0:
                        StoreInfo storeInfo = CustomApplication.customApplication.getStoreInfo();
                        if (storeInfo == null) {
                            ViewUtils.sendActivity(StartActivity.this, StoreLoginActivity.class);
                        } else {
                            ViewUtils.sendActivity(StartActivity.this, Store_MainActivity.class);
                            CustomApplication.customApplication.bindAccount(storeInfo.getLoginEmail());
                        }
                        break;
                    default:
                        UserInfo userinfo = CustomApplication.customApplication.getUserinfo();
                        if (userinfo == null) {
                            ViewUtils.sendActivity(StartActivity.this, GuideActivity.class);
                        } else {
                            openIMLogin(userinfo.getPhone(), userinfo.getPassword());
                            CustomApplication.customApplication.bindAccount(userinfo.getNickName());
                            ViewUtils.sendActivity(StartActivity.this, MainActivity.class);
                        }
                }
                finish();
            }

        }, 1000);
    }
}
