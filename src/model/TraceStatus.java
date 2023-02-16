package model;

import java.sql.Timestamp;

public class TraceStatus {
    private long id;
    private long tenantid;
    private Timestamp starttime;
    private Timestamp timestamp;
    private String status;
    public TraceStatus(long id, long tenantid, Timestamp starttime, Timestamp timestamp, String status) {
        this.id = id;
        this.tenantid = tenantid;
        this.starttime = starttime;
        this.timestamp = timestamp;
        this.status = status;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getTenantid() {
        return tenantid;
    }
    public void setTenantid(long tenantid) {
        this.tenantid = tenantid;
    }
    public Timestamp getStarttime() {
        return starttime;
    }
    public void setStarttime(Timestamp starttime) {
        this.starttime = starttime;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
