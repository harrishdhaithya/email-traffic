package singleton;

import dao.ConfigDao;
import dao.CredentialDao;
import dao.TenantDao;

public class Singleton {
    private static ConfigDao cdao = null;
    private static CredentialDao credDao = null;
    private static TenantDao tdao = null;
    public static ConfigDao getConfigDao(){
        if(cdao==null){
            cdao = new ConfigDao();
        }
        return cdao;
    }
    public static CredentialDao getCredentialDao(){
        if(credDao==null){
            credDao = new CredentialDao();
        }
        return credDao;
    }
    public static TenantDao getTenantDao(){
        if(tdao==null){
            tdao=new TenantDao();
        }
        return tdao;
    }
}
