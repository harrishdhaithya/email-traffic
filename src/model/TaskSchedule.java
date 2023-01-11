package model;

public class TaskSchedule {
    private long id;
    private long tenantid;
    private long count;
    public TaskSchedule(long tenantid, long count) {
        this.tenantid = tenantid;
        this.count = count;
    }
    public TaskSchedule(long id, long tenantid, long count) {
        this.id = id;
        this.tenantid = tenantid;
        this.count = count;
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
    public long getCount() {
        return count;
    }
    public void setCount(long count) {
        this.count = count;
    }
    
}
