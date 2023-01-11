package utils;
import dao.CredentialDao;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import model.Credential;

public class DBCredUtil implements ICredUtil {
    private long tenant_id;
    public DBCredUtil(long tenant_id){
        this.tenant_id=tenant_id;
    }
    @Override
    public ExchangeCredentials getRandomCredPair() {
        CredentialDao cdao = CredentialDao.getInstance();
        Credential c = cdao.getRandomCredential(tenant_id);
        if(c==null){
            return null;
        }
        return new WebCredentials(c.getEmail(),c.getPassword());
    }
    @Override
    public String getRandomReciever() {
        CredentialDao cdao = CredentialDao.getInstance();
        String s = cdao.getRandomReciever(tenant_id);
        return s;
    }
}
