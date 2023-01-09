package model;

import java.util.Set;

public class Mail {
    private String from;
    private Set<String> toList;
    private String subject;
    private String content;
    private boolean status;
    public Mail(String from, Set<String> toList, String subject, String content) {
        this.from = from;
        this.toList = toList;
        this.subject = subject;
        this.content = content;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
    public Set<String> getToList() {
        return toList;
    }
    public void setToList(Set<String> toList) {
        this.toList = toList;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setStatus(boolean status){
        this.status=status;
    }
    public boolean getStatus(){
        return this.status;
    }
}
