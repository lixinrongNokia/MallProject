package iliker.mall.depth;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.fjl.widget.ToastFactory;

import cn.iliker.mall.R;
import iliker.utils.ParsJsonUtil;
import iliker.utils.email.MailSenderInfo;
import iliker.utils.email.SimpleMailSender;

class EndPager extends Basepager implements View.OnClickListener {
    private EditText emailaddress;

    EndPager(Activity activity) {
        super(activity);
    }


    public void initData() {
        title.setText("恭喜！测试已完成!");
        View itemview = View.inflate(activity, R.layout.sweetheart_layout, null);
        itemview.findViewById(R.id.resultView).setOnClickListener(this);
        emailaddress = (EditText) itemview.findViewById(R.id.emailaddress);
        contentPanel.addView(itemview);
    }

    @Override
    public void onClick(View v) {
        final String email = emailaddress.getText().toString().trim();
        if (!TextUtils.isEmpty(email)) {
            if (!ParsJsonUtil.isEmail(email)) {
                ToastFactory.getMyToast("请填写正确的邮箱地址").show();
                return;
            }
            new Thread() {
                @Override
                public void run() {
                    MailSenderInfo mailInfo = new MailSenderInfo();
                    mailInfo.setMailServerHost("smtp.163.com");
                    mailInfo.setMailServerPort("25");
                    mailInfo.setValidate(true);
                    mailInfo.setUserName("18680602795@163.com");
                    mailInfo.setPassword("ywrkviblnozlxobn");// 您的邮箱密码
                    mailInfo.setFromAddress("18680602795@163.com");
                    mailInfo.setToAddress(email);
                    mailInfo.setSubject("爱内秀身形测试");
                    mailInfo.setContent("尊敬的客户，感谢你参与本公司用户身形测试，参与奖品请登录客户端领取，谢谢！");
                    // 这个类主要来发送邮件
                    // sms.sendTextMail(mailInfo);//发送文体格式
                    boolean isOk = SimpleMailSender.sendHtmlMail(mailInfo);// 发送html格式
                    if (isOk) ToastFactory.getMyToast("发送成功").show();
                    else ToastFactory.getMyToast("待投递状态").show();
                }
            }.start();
        }
        Intent intent = new Intent(activity, Result_Activity.class);
        activity.startActivity(intent);
        activity.finish();
    }
}
