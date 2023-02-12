package model;

public class Credential {
    private long id;
    private String email;
    private String password;
    private long tenantId;
    private String status;
    public Credential(long id, String email, String password, long tenantId,String status) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.tenantId = tenantId;
        this.status = status;
    }
    public Credential(String email, String password, long tenantId) {
        this.email = email;
        this.password = password;
        this.tenantId = tenantId;
    }
    public Credential(String email,String password){
        this.email = email;
        this.password = password;
    }
    public long getTenantId() {
        return tenantId;
    }
    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }
    public long getId(){
        return id;
    }
    public void setId(long id){
        this.id = id;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return status;
    }
}
