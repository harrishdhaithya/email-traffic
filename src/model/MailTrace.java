package model;

public class MailTrace {
    private long id;
    private long traceid;
    private String sender;
    private String receiver;
    private String subject;
    private String timestamp;
    private String messageId;
    private String messageTraceId;
    private String status;
    public MailTrace(String sender, String receiver, String subject, String timestamp,String messageId,String messageTraceId,String status) {
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.timestamp = timestamp;
        this.messageId = messageId;
        this.messageTraceId = messageTraceId;
        this.status = status;
    }
    public MailTrace(long id, long traceid, String sender, String receiver, String subject,
            String timestamp, String messageId, String messageTraceId,String status) {
        this.id = id;
        this.traceid = traceid;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.timestamp = timestamp;
        this.messageId = messageId;
        this.messageTraceId = messageTraceId;
        this.status = status;
    }
    public MailTrace( long traceid, String sender, String receiver, String subject, String timestamp,
String messageId, String messageTraceId,String status) {
        this.traceid = traceid;
        this.sender = sender;
        this.receiver = receiver;
        this.subject = subject;
        this.timestamp = timestamp;
        this.messageId = messageId;
        this.messageTraceId = messageTraceId;
        this.status = status;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getReceiver() {
        return receiver;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public String getMessageId() {
        return messageId;
    }
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    public String getMessageTraceId() {
        return messageTraceId;
    }
    public void setMessageTraceId(String messageTraceId) {
        this.messageTraceId = messageTraceId;
    }
    public long getTraceid() {
        return traceid;
    }
    public void setTraceid(long traceid) {
        this.traceid = traceid;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return status;
    }
    @Override
    public String toString() {
        return "MailTrace [id=" + id + ", traceid=" + traceid + ", sender=" + sender + ", receiver=" + receiver
                + ", subject=" + subject + ", timestamp=" + timestamp + ", messageId=" + messageId + ", messageTraceId="
                + messageTraceId + "]";
    }
    
}
