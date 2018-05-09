package cn.iliker.mall.alipush;


/**
 * 推送消息实体
 */

public class MessageEntity implements java.io.Serializable {
    private Integer id;
    private String messageId;//消息ID
    private String receiver;//消息接收者
    private String createTime;//创建时间
    private String modifyTime;//更新时间
    private String messageTitle;//消息抬头
    private String messageContent;//消息内容
    private String targetActivity;//准备打开的目标页面：Activity
    private String targetURL;//准备打开的URL
    private String unPackOrderID;//订单id
    private String otherTips;//预留的其他参数位置

    public MessageEntity() {
    }

    public MessageEntity(Integer id, String messageId, String createTime, String messageTitle, String messageContent, String targetActivity, String targetURL, String unPackOrderID) {
        this.id = id;
        this.messageId = messageId;
        this.createTime = createTime;
        this.messageTitle = messageTitle;
        this.messageContent = messageContent;
        this.targetActivity = targetActivity;
        this.targetURL = targetURL;
        this.unPackOrderID = unPackOrderID;
    }

    public MessageEntity(String messageId, String receiver, String createTime, String messageTitle, String messageContent) {
        this.messageId = messageId;
        this.receiver = receiver;
        this.createTime = createTime;
        this.messageTitle = messageTitle;
        this.messageContent = messageContent;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getTargetActivity() {
        return targetActivity;
    }

    public void setTargetActivity(String targetActivity) {
        this.targetActivity = targetActivity;
    }

    public String getTargetURL() {
        return targetURL;
    }

    public void setTargetURL(String targetURL) {
        this.targetURL = targetURL;
    }

    public String getUnPackOrderID() {
        return unPackOrderID;
    }

    public void setUnPackOrderID(String unPackOrderID) {
        this.unPackOrderID = unPackOrderID;
    }

    public String getOtherTips() {
        return otherTips;
    }

    public void setOtherTips(String otherTips) {
        this.otherTips = otherTips;
    }
}
