package iliker.entity;

/**
 * Created by LIXINRONG on 2016/8/27.
 */
public class Social {
    private int uid;
    private String photoAlbum;
    private String emotionalState;
    private String job;
    private String hometown;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPhotoAlbum() {
        return photoAlbum;
    }

    public void setPhotoAlbum(String photoAlbum) {
        this.photoAlbum = photoAlbum;
    }

    public String getEmotionalState() {
        return emotionalState;
    }

    public void setEmotionalState(String emotionalState) {
        this.emotionalState = emotionalState;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }
}
