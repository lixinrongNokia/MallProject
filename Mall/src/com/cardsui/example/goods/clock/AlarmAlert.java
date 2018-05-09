package com.cardsui.example.goods.clock;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;

import com.cardsui.example.goods.FlashSaleActivity;

import cn.iliker.mall.R;

public class AlarmAlert extends Activity {
    private static MediaPlayer mediaPlayer;

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        mediaPlayer = MediaPlayer.create(this, R.raw.hangouts_incoming_call);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        new AlertDialog.Builder(AlarmAlert.this)
                .setIcon(R.drawable.clock)
                .setTitle(intent.getStringExtra("title"))
                .setMessage(intent.getStringExtra("content"))
                .setPositiveButton("去抢购",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                dialog.dismiss();
                                stop();
                                Intent intent1 = new Intent(AlarmAlert.this, FlashSaleActivity.class);
                                startActivity(intent1);
                                finish();
                            }
                        }).setNegativeButton("关闭", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                stop();
                finish();
            }
        }).show();
    }

    private static void stop() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

}
