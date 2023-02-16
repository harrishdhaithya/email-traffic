package auth;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;
import dao.ConfigDao;
import dao.TenantDao;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import model.Tenant;


/*
 *   Source: https://github.com/Azure-Samples/ms-identity-msal-java-samples/blob/main/2.%20Client-Side%20Scenarios/Username-Password-Flow/src/main/java/UsernamePasswordFlow.java
 */

public class CredentialProvider {
    private static final String BASE_URL = "https://login.microsoftonline.com/";
    private static CredentialProvider cp = null;
    private static final Logger logger = Logger.getLogger(CredentialProvider.class.getName()); 
    public static CredentialProvider getInstance(){
        if(cp==null){
            cp = new CredentialProvider();
        }
        return cp;
    }
    
    private static IAuthenticationResult acquireTokenUsernamePassword(PublicClientApplication pca,
                                                                      Set<String> scope,
                                                                      String username,
                                                                      String password) throws Exception {
        IAuthenticationResult result;
        UserNamePasswordParameters parameters =
                        UserNamePasswordParameters
                                .builder(scope, username, password.toCharArray())
                                .build();
        result = pca.acquireToken(parameters).join();
        logger.info("Access token generated for user "+username);
        return result;
    }
    public String getToken(String username,String password,long tenantid,Set<String> scopes) throws Exception{
        TenantDao tdao = TenantDao.getInstance();
        Tenant tenant = tdao.getTenant(tenantid);
        PublicClientApplication pca;
        IAuthenticationResult result=null;
        try {
            pca = PublicClientApplication.builder(tenant.getAppClientId())
                    .authority(BASE_URL+tenant.getName())
                    .build();
            result = acquireTokenUsernamePassword(pca, scopes, username, password);
        } catch (Exception e) {
            logger.warning(e.getMessage());
            throw new Exception("Failed to Acquire Token...");
        }
        return result.accessToken();
    }
    public String getAdminToken(long tenantid,Set<String> scopes) throws Exception{
        TenantDao tdao = TenantDao.getInstance();
        Tenant t = tdao.getTenant(tenantid);
        String adminuName = t.getAdminEmail();
        String adminPword = t.getAdminPassword();
        if(adminuName==null||adminPword==null){
            throw new Exception("Please Add Admin Credentials in tenant configuration...");
        }
        return getToken(adminuName, adminPword, tenantid, scopes);
    }
    public ExchangeCredentials getCredential(String username,String password,long tenantid) throws Exception{
        if(username==null||password==null){
            throw new Exception("No Credential found");
        }
        ExchangeCredentials ec = null;
        ConfigDao confDao = ConfigDao.getInstance();
        String scope = confDao.getConfig("scope").getPropValue();
        Set<String> scopes = new HashSet<>();
        scopes.add(scope);
        try {
            ec = new OAuthCredentials(getToken(username, password, tenantid, scopes));
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Unable to authenticate...");
        }
        return ec;
    }
}
