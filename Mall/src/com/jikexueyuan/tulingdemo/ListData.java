package com.jikexueyuan.tulingdemo;

public class ListData {

    static final int SEND = 1;
    static final int RECEIVER = 2;
    private String content;
    private int flag;
    private String time;
    private String headimg;

    ListData(String content, int flag, String time, String headimg) {
        setContent(content);
        setFlag(flag);
        setTime(time);
        setHeadimg(headimg);
    }

    public String getContent() {
        return content;
    }

    private void setContent(String content) {
        this.content = content;
    }

    int getFlag() {
        return flag;
    }

    private void setFlag(int flag) {
        this.flag = flag;
    }

    public String getTime() {
        return time;
    }

    private void setTime(String time) {
        this.time = time;
    }

    public String getHeadimg() {
        return headimg;
    }

    private void setHeadimg(String headimg) {
        this.headimg = headimg;
    }
}
