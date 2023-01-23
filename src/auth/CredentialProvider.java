package auth;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;
import dao.ConfigDao;
import dao.TenantDao;
import exception.CredentialException;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import model.Tenant;

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
                                                                      IAccount account,
                                                                      String username,
                                                                      String password) throws CredentialException {
        IAuthenticationResult result;
        try {
            SilentParameters silentParameters = SilentParameters.builder(scope)
                                                                .account(account)
                                                                .build();
            result = pca.acquireTokenSilently(silentParameters).join();
        } catch (Exception ex) {
            if (ex.getCause() instanceof MsalException) {
                UserNamePasswordParameters parameters =
                        UserNamePasswordParameters
                                .builder(scope, username, password.toCharArray())
                                .build();
                result = pca.acquireToken(parameters).join();
            } else {
                ex.printStackTrace();
                throw new CredentialException("Unable to Authenticate...");
            }
        }

        return result;
    }

    private static IAccount getAccountByUsername(Set<IAccount> accounts, String username) {
        if (accounts.isEmpty()) {
            logger.info("No accounts in cache");
        } else {
            System.out.println("==Accounts in cache: " + accounts.size());
            for (IAccount account : accounts) {
                if (account.username().equals(username)) {
                    return account;
                }
            }
        }
        return null;
    }
    public ExchangeCredentials getCredential(String username,String password,long tenantid) throws CredentialException{
        ExchangeCredentials ec = null;
        TenantDao tdao = TenantDao.getInstance();
        Tenant tenant = tdao.getTenant(tenantid);
        ConfigDao confDao = ConfigDao.getInstance();
        String scope = confDao.getConfig("scope").getPropValue();
        Set<String> scopes = new HashSet<>();
        scopes.add(scope);
        PublicClientApplication pca;
        try {
            pca = PublicClientApplication.builder(tenant.getAppClientId())
                    .authority(BASE_URL+tenant.getName())
                    .build();
            Set<IAccount> accountsInCache = pca.getAccounts().join();
            IAccount account = getAccountByUsername(accountsInCache, username);
            IAuthenticationResult result = acquireTokenUsernamePassword(pca, scopes, account, username, password);
            System.out.println(result.accessToken());
            ec = new OAuthCredentials(result.accessToken());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new CredentialException("Unable to authenticate...");

        }
        return ec;
    }
}
