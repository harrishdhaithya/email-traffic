package model;

public class Credential {
    private long id;
    private String email;
    private String password;
    private long tenantId;
    public Credential(long id, String email, String password, long tenantId) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.tenantId = tenantId;
    }
    public Credential(String email, String password, long tenantId) {
        this.email = email;
        this.password = password;
        this.tenantId = tenantId;
    }
    public Credential(String email,String password){
        System.out.println(email+" "+password);
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
    @Override
    public String toString() {
        return "Credential [id=" + id + ", email=" + email + ", password=" + password + ", tenantId=" + tenantId + "]";
    }
}
