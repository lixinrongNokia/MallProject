package countdowntimer;

class Utils {

    /**
     * 把秒数转换成时分秒的形式
     */
    static TimeForm getTimeForm(int secondNum) {

        TimeForm time = new TimeForm();

        int hour;
        int minute;
        int second;
        if (secondNum <= 0) {//时分秒为0
            time.hour = 0;
            time.minute = 0;
            time.second = 0;
        } else {
            minute = secondNum / 60;
            if (minute < 60) {//时为0，只有分秒
                second = secondNum % 60;

                time.hour = 0;
                time.minute = minute;
                time.second = second;

            } else {//有时、可能有分秒
                hour = minute / 60;//计算时                
                minute = minute % 60;//计算分
                second = secondNum - hour * 3600 - minute * 60;//计算秒          

                time.hour = hour;
                time.minute = minute;
                time.second = second;
            }
        }

        return time;
    }
}
