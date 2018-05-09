package iliker.mall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class FeedbackActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		toIntent7();
	}

	/* 发邮件 */
	private void toIntent7() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL,
				new String[] { "wdhtct8788@Live.com" });
		intent.putExtra(Intent.EXTRA_SUBJECT, "用户反馈");
		intent.setType("message/rfc822");
		startActivity(Intent.createChooser(intent, "请选择邮件客户端"));
		finish();
	}
}
