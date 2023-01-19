
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.MsalException;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.aad.msal4j.UserNamePasswordParameters;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import model.Mail;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class UserPass {

    private static String authority;
    private static Set<String> scope;
    private static String clientId;
    private static String username;
    private static String password;
    private static String serviceUrl = "https://outlook.office365.com/EWS/Exchange.asmx";
    private static boolean sendEmail(ExchangeService eserv,ExchangeCredentials ecred, Mail mail){
        try {
            eserv.setUrl(new URI(serviceUrl));
            eserv.setCredentials(ecred);
            eserv.setTraceEnabled(true);
            EmailMessage message = new EmailMessage(eserv);
            message.setSubject(mail.getSubject());
            message.setBody(MessageBody.getMessageBodyFromText(mail.getContent()));
            for(String toEmail: mail.getToList()){
                message.getToRecipients().add(toEmail);
            }
            message.send();
            message=null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void main(String args[]) throws Exception {

        setUpSampleData();

        PublicClientApplication pca = PublicClientApplication.builder(clientId)
                .authority(authority)
                .build();

        //Get list of accounts from the application's token cache, and search them for the configured username
        //getAccounts() will be empty on this first call, as accounts are added to the cache when acquiring a token
        Set<IAccount> accountsInCache = pca.getAccounts().join();
        IAccount account = getAccountByUsername(accountsInCache, username);

        //Attempt to acquire token when user's account is not in the application's token cache
        IAuthenticationResult result = acquireTokenUsernamePassword(pca, scope, account, username, password);
        System.out.println("Account username: " + result.account().username());
        System.out.println("Access token:     " + result.accessToken());
        System.out.println("Id token:         " + result.idToken());
        System.out.println();

        accountsInCache = pca.getAccounts().join();
        account = getAccountByUsername(accountsInCache, username);

        //Attempt to acquire token again, now that the user's account and a token are in the application's token cache
        result = acquireTokenUsernamePassword(pca, scope, account, username, password);
        System.out.println("Account username: " + result.account().username());
        System.out.println("Access token:     " + result.accessToken());
        System.out.println("Id token:         " + result.idToken());
        ExchangeService eserv = new ExchangeService();
        ExchangeCredentials ecred = new BearerTokenCredentials(result.accessToken());
        // eserv.setImpersonatedUserId(new ImpersonatedUserId(ConnectingIdType.SmtpAddress, username));
        Set<String> toList = new HashSet<>();
        toList.add("testing_mailbox_6997@1wqf3d.onmicrosoft.com");
        Mail m = new Mail(password, toList, "Subject", "Content");
        sendEmail(eserv, ecred, m);
    }

    private static IAuthenticationResult acquireTokenUsernamePassword(PublicClientApplication pca,
                                                                      Set<String> scope,
                                                                      IAccount account,
                                                                      String username,
                                                                      String password) throws Exception {
        IAuthenticationResult result;
        try {
            SilentParameters silentParameters =
                    SilentParameters
                            .builder(scope)
                            .account(account)
                            .build();
            // Try to acquire token silently. This will fail on the first acquireTokenUsernamePassword() call
            // because the token cache does not have any data for the user you are trying to acquire a token for
            result = pca.acquireTokenSilently(silentParameters).join();
            System.out.println("==acquireTokenSilently call succeeded");
        } catch (Exception ex) {
            if (ex.getCause() instanceof MsalException) {
                System.out.println("==acquireTokenSilently call failed: " + ex.getCause());
                UserNamePasswordParameters parameters =
                        UserNamePasswordParameters
                                .builder(scope, username, password.toCharArray())
                                .build();
                // Try to acquire a token via username/password. If successful, you should see
                // the token and account information printed out to console
                result = pca.acquireToken(parameters).join();
                System.out.println("==username/password flow succeeded");
            } else {
                // Handle other exceptions accordingly
                throw ex;
            }
        }

        return result;
    }

    /**
     * Helper function to return an account from a given set of accounts based on the given username,
     * or return null if no accounts in the set match
     */
    private static IAccount getAccountByUsername(Set<IAccount> accounts, String username) {
        if (accounts.isEmpty()) {
            System.out.println("==No accounts in cache");
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

    /**
     * Helper function unique to this sample setting. In a real application these wouldn't be so hardcoded, for example
     * values such as username/password would come from the user, and different users may require different scopes
    **/
    private static void setUpSampleData() throws IOException {
        // Load properties file and set properties used throughout the sample
        Properties properties = new Properties();
        properties.load(new FileReader("application.properites"));
        authority = properties.getProperty("AUTHORITY");
        scope = Collections.singleton(properties.getProperty("SCOPE"));
        clientId = properties.getProperty("CLIENT_ID");
        username = properties.getProperty("USER_NAME");
        password = properties.getProperty("USER_PASSWORD");
    }
}