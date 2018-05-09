
package countdowntimer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import cn.iliker.mall.R;

/**
 * 倒计时组件
 */
@SuppressLint("HandlerLeak")
public class CountDownTimerView extends LinearLayout {

    // 小时，十位
    private final TextView tv_hour_decade;
    // 小时，个位
    private final TextView tv_hour_unit;
    // 分钟，十位
    private final TextView tv_min_decade;
    // 分钟，个位
    private final TextView tv_min_unit;
    // 秒，十位
    private final TextView tv_sec_decade;
    // 秒，个位
    private final TextView tv_sec_unit;

    /**
     * 总共倒计时的时间(单位：秒，为了更加精确的得到倒计时到达，用这个时间来计算)
     */
    private int mSecondNum = -1;
    // 计时器
    private Timer timer;
    private TimerTask mTimerTask;
    private OnTimeToListener onTimeToListener = null;

    private final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            mSecondNum--;
            if (mSecondNum == 0) {
                if (onTimeToListener != null) {
                    onTimeToListener.handleTimeToEvent();
                }
            }
            countDown();
        }
    };

    public CountDownTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_countdowntimer, this);

        tv_hour_decade = (TextView) view.findViewById(R.id.tv_hour_decade);
        tv_hour_unit = (TextView) view.findViewById(R.id.tv_hour_unit);
        tv_min_decade = (TextView) view.findViewById(R.id.tv_min_decade);
        tv_min_unit = (TextView) view.findViewById(R.id.tv_min_unit);
        tv_sec_decade = (TextView) view.findViewById(R.id.tv_sec_decade);
        tv_sec_unit = (TextView) view.findViewById(R.id.tv_sec_unit);

    }

    /**
     * 开始计时
     */
    public void startCountDown() {

        if (timer == null) {
            timer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            };
        }
        timer.schedule(mTimerTask, 0, 1000);
    }

    /**
     * 停止计时
     */
    public void stopCountDown() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    /**
     * 设置倒计时的时长(时分秒的数据0~59)
     *
     * @param hour 时
     * @param min  分
     * @param sec  秒
     */
    private void setTime(int hour, int min, int sec) {

        if (hour >= 60 || min >= 60 || sec >= 60 || hour < 0 || min < 0
                || sec < 0) {
            throw new RuntimeException("Time format is error,please check out your code.");
        }

        int hour_decade = hour / 10;
        int hour_unit = hour - hour_decade * 10;

        int min_decade = min / 10;
        int min_unit = min - min_decade * 10;

        int sec_decade = sec / 10;
        int sec_unit = sec - sec_decade * 10;

        tv_hour_decade.setText(String.valueOf(hour_decade));
        tv_hour_unit.setText(String.valueOf(hour_unit));
        tv_min_decade.setText(String.valueOf(min_decade));
        tv_min_unit.setText(String.valueOf(min_unit));
        tv_sec_decade.setText(String.valueOf(sec_decade));
        tv_sec_unit.setText(String.valueOf(sec_unit));

    }

    /**
     * 设置倒计时的时长，推荐用这个方法(传入秒数，秒数不能大于215999，即59时59分59秒)
     *
     * @param secondNum 秒数
     */
    public void setTime(int secondNum) {

        if (secondNum > 215999) {
            throw new RuntimeException("Time format is error,please check out your code.");
        }
        mSecondNum = secondNum;
        TimeForm time = Utils.getTimeForm(secondNum);
        setTime(time.hour, time.minute, time.second);
    }

    /**
     * 倒计时
     */
    private void countDown() {

        /**
         * 对界面上的时分秒数据,从秒往时操作
         */
        if (isCarry4Unit(tv_sec_unit)) {//先对秒的个位数自减操作并判断是否需要进位操作十位
            if (isCarry4Decade(tv_sec_decade)) {//如果需要进位到秒的十位数操作

                if (isCarry4Unit(tv_min_unit)) {//

                    if (isCarry4Decade(tv_min_decade)) {

                        if (isCarry4Unit(tv_hour_unit)) {

                            if (isCarry4Decade(tv_hour_decade)) {

                                tv_hour_decade.setText("0");
                                tv_hour_unit.setText("0");
                                tv_min_decade.setText("0");
                                tv_min_unit.setText("0");
                                tv_sec_decade.setText("0");
                                tv_sec_unit.setText("0");
                                stopCountDown();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 十位的数字减一，并判断是否需要进位(时分秒以2位数表示,这里对10位数进行操作)
     */
    private boolean isCarry4Decade(TextView tv) {

        int time = Integer.valueOf(tv.getText().toString());
        time = time - 1;
        if (time < 0) {
            time = 5;
            tv.setText(String.valueOf(time));
            return true;
        } else {
            tv.setText(String.valueOf(time));
            return false;
        }

    }

    /**
     * 个位的数字减一，并判断是否需要进位(时分秒以2位数表示，这里对个位数进行操作)
     */
    private boolean isCarry4Unit(TextView tv) {
        int time = Integer.valueOf(tv.getText().toString());
        time = time - 1;
        if (time < 0) {
            time = 9;
            tv.setText(String.valueOf(time));
            return true;
        } else {
            tv.setText(String.valueOf(time));
            return false;
        }
    }

    /**
     * 设置时间到达时的监听器
     */
    public void setOnTimeToListener(OnTimeToListener onTimeToListener) {
        this.onTimeToListener = onTimeToListener;
    }

    /**
     * 时间到达时的监听器
     */
    public interface OnTimeToListener {
        /**
         * 处理时间到达时事件
         */
        void handleTimeToEvent();
    }
}
