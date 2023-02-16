package model;

public class Tenant {
    private long id;
    private String name;
    // private String azureTenantId;
    private String appClientId;
    private String adminEmail;
    private String adminPassword;
    public Tenant(String name,String appClientId,String adminEmail,String adminPassword){
        this.name = name;
        // this.azureTenantId = azureTenantId;
        this.appClientId = appClientId;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }
    public Tenant(long id,String name,String appClientId,String adminEmail,String adminPassword){
        this.id = id;
        this.name = name;
        this.appClientId = appClientId;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }
    public Tenant(String name,String appClientId){
        this.name = name;
        // this.azureTenantId = azureTenantId;
        this.appClientId = appClientId;
    }
    public Tenant(long id, String name,String appClientId){
        this.id = id;
        this.name = name;
        // this.azureTenantId = azureTenantId;
        this.appClientId = appClientId;
    }
    public void setId(long id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public long getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getAppClientId() {
        return appClientId;
    }
    public void setAppClientId(String appClientId) {
        this.appClientId = appClientId;
    }
    public String getAdminPassword() {
        return adminPassword;
    }
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
    public String getAdminEmail() {
        return adminEmail;
    }
    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }
    @Override
    public String toString() {
        return "Tenant [id=" + id + ", name=" + name + ", appClientId=" + appClientId + ", adminEmail=" + adminEmail
                + ", adminPassword=" + adminPassword + "]";
    }
    
}
