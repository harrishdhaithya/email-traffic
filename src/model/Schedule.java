package model;

public class Schedule {
    private long id;
    private String name;
    private String time;
    private long status;
    private long count;
    private long tenant_id;
    public Schedule(long id, String name,long tenant_id,String time, long status,long count) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.status = status;
        this.count = count;
        this.tenant_id = tenant_id;
    }
    public long getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public long getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public void setCount(long count){
        this.count = count;
    }
    public long getCount(){
        return count;
    }
    public long getTenantId() {
        return tenant_id;
    }
    public void setTenantId(long tenant_id){
        this.tenant_id = tenant_id;
    }
    @Override
    public String toString() {
        return "Schedule [id=" + id + ", name=" + name + ", time=" + time + ", status=" + status + "]";
    }
}
