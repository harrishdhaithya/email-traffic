package model;

import java.sql.Timestamp;

public class TrafficHistory {
    private long id;
    private long tenantid;
    private long successCount;
    private long failureCount;
    private long totalCount;
    private Timestamp starttime;
    private Timestamp endtime;
    public TrafficHistory(long id, long tenantid, long successCount, long failureCount, long totalCount,
            Timestamp starttime, Timestamp endtime) {
        this.id = id;
        this.tenantid = tenantid;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.totalCount = totalCount;
        this.starttime = starttime;
        this.endtime = endtime;
    }
    public TrafficHistory(long tenantid, long successCount, long failureCount, long totalCount, Timestamp starttime,
            Timestamp endtime) {
        this.tenantid = tenantid;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.totalCount = totalCount;
        this.starttime = starttime;
        this.endtime = endtime;
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
    public long getSuccessCount() {
        return successCount;
    }
    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }
    public long getFailureCount() {
        return failureCount;
    }
    public void setFailureCount(long failureCount) {
        this.failureCount = failureCount;
    }
    public long getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
    public Timestamp getStarttime() {
        return starttime;
    }
    public void setStarttime(Timestamp starttime) {
        this.starttime = starttime;
    }
    public Timestamp getEndtime() {
        return endtime;
    }
    public void setEndtime(Timestamp endtime) {
        this.endtime = endtime;
    }
    @Override
    public String toString() {
        return "TrafficHistory [id=" + id + ", tenantid=" + tenantid + ", successCount=" + successCount
                + ", failureCount=" + failureCount + ", totalCount=" + totalCount + ", starttime=" + starttime
                + ", endtime=" + endtime + "]";
    }
    
}
