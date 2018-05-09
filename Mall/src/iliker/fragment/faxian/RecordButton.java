package iliker.fragment.faxian;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fjl.widget.ToastFactory;

import java.io.File;
import java.io.IOException;

import cn.iliker.mall.R;

public class RecordButton extends Button {

    public RecordButton(Context context) {
        super(context);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setSavePath(String path) {
        mFileName = path;
    }

    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        finishedListener = listener;
    }

    private String mFileName = null;

    private OnFinishedRecordListener finishedListener;

    private static final int MIN_INTERVAL_TIME = 2000;// 2s
    private long startTime;

    private Dialog recordIndicator;

    private final static int[] res = {R.drawable.ic_audio_comment_sound_01,
            R.drawable.ic_audio_comment_sound_02,
            R.drawable.ic_audio_comment_sound_03,
            R.drawable.ic_audio_comment_sound_04,
            R.drawable.ic_audio_comment_sound_05,
            R.drawable.ic_audio_comment_sound_06,
            R.drawable.ic_audio_comment_sound_07};

    private ImageView view;

    private MediaRecorder recorder;

    private ObtainDecibelThread thread;

    private Handler volumeHandler;

    private void init() {
        volumeHandler = new ShowVolumeHandler();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mFileName == null)
            return false;

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initDialogAndStartRecord();
                break;
            case MotionEvent.ACTION_UP:
                finishRecord();
                break;
            case MotionEvent.ACTION_CANCEL:// 当手指移动到view外面，会cancel
                cancelRecord();
                break;
        }

        return true;
    }

    private void initDialogAndStartRecord() {

        startTime = System.currentTimeMillis();
        recordIndicator = new Dialog(getContext(),
                R.style.like_toast_dialog_style);
        view = new ImageView(getContext());
        view.setImageResource(R.drawable.ic_audio_comment_sound_01);
        recordIndicator.setContentView(view, new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        recordIndicator.setOnDismissListener(onDismiss);
        Window window = recordIndicator.getWindow();
        if (window != null) {
            LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.CENTER;
        }
        try {
            startRecording();
            recordIndicator.show();
        } catch (RuntimeException e) {
            ToastFactory.getMyToast("录音功能被限制").show();
        }
    }

    private void finishRecord() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopRecording();
        recordIndicator.dismiss();

        long intervalTime = System.currentTimeMillis() - startTime;
        if (intervalTime < MIN_INTERVAL_TIME) {
            Toast.makeText(getContext(), "时间太短！", Toast.LENGTH_SHORT).show();
            File file = new File(mFileName);
            file.delete();
            return;
        }
        if (finishedListener != null)
            finishedListener.onFinishedRecord(mFileName);
    }

    private void cancelRecord() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }
        stopRecording();
        recordIndicator.dismiss();

        Toast.makeText(getContext(), "取消录音！", Toast.LENGTH_SHORT).show();
        File file = new File(mFileName);
        file.delete();
    }

    private void startRecording() throws RuntimeException{
        recorder = new MediaRecorder();
        try {
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(mFileName);
            recorder.prepare();
            recorder.start();
            thread = new ObtainDecibelThread();
            thread.start();
        } catch (IllegalStateException e) {
            throw  new RuntimeException();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void stopRecording() {
        if (thread != null) {
            thread.exit();
            thread = null;
        }
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (recorder == null || !running) {
                    break;
                }
                int x = recorder.getMaxAmplitude();
                if (x != 0) {
                    int f = (int) (10 * Math.log(x) / Math.log(10));
                    if (f < 26)
                        volumeHandler.sendEmptyMessage(0);
                    else if (f < 32)
                        volumeHandler.sendEmptyMessage(1);
                    else if (f < 38)
                        volumeHandler.sendEmptyMessage(2);
                    else
                        volumeHandler.sendEmptyMessage(3);

                }

            }
        }

    }

    private final OnDismissListener onDismiss = new OnDismissListener() {

        @Override
        public void onDismiss(DialogInterface dialog) {
            stopRecording();
        }
    };

    private class ShowVolumeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            view.setImageResource(res[msg.what]);
        }
    }

    public interface OnFinishedRecordListener {
        void onFinishedRecord(String audioPath);
    }

}
