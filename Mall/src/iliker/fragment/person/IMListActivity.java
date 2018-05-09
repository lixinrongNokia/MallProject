package iliker.fragment.person;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.conversation.IYWConversationListener;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWP2PConversationBody;
import com.alibaba.mobileim.lib.presenter.conversation.P2PConversation;

import java.util.List;

import cn.iliker.mall.R;

import static com.iliker.application.CustomApplication.getmIMKit;

public class IMListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, IYWConversationListener {
    private ListView imlv;
    private TextView emptymsg;
    private IYWConversationService conversationService;
    private IMAdapter imAdapter;
    private List<YWConversation> ywConversationList;
    private YWIMKit ywimKit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.im_group_layout);
        imlv = (ListView) findViewById(R.id.imlv);
        imlv.setOnItemClickListener(this);
        emptymsg = (TextView) findViewById(R.id.emptymsg);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
        }
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "清空消息");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                this.finish();
                break;
            case 1:
                // 清空所有会话
                conversationService.deleteAllConversation();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        ywimKit = getmIMKit();
        if (ywimKit != null) {
            conversationService = ywimKit.getConversationService();
            // 添加会话列表变更监听
            conversationService.addConversationListener(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sessionList();
    }

    private void sessionList() {
        ywConversationList = conversationService.getConversationList();
        if (ywConversationList != null) {
            emptymsg.setVisibility(View.GONE);
            imlv.setVisibility(View.VISIBLE);
            imAdapter = new IMAdapter(ywConversationList, this);
            imlv.setAdapter(imAdapter);
        } else {
            imlv.setVisibility(View.GONE);
            emptymsg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        YWConversation ywConversation = ywConversationList.get(position);
        if (ywConversation instanceof P2PConversation) {
            P2PConversation p2PConversation = (P2PConversation) ywConversation;
            YWP2PConversationBody body = (YWP2PConversationBody) p2PConversation.getConversationBody();
            IYWContact contact = body.getContact();
            Intent intent = ywimKit.getChattingActivityIntent(contact.getUserId(), getString(R.string.openIMKey));
            startActivity(intent);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conversationService != null) {
            conversationService.removeConversationListener(this);
        }
    }

    @Override
    public void onItemUpdated() {
        if (imAdapter != null) {
            imAdapter.notifyDataSetChanged();
        }
    }
}
