package utils;
import dao.CredentialDao;
import model.Credential;

public class DBCredUtil implements ICredUtil {
    private long tenant_id;
    public DBCredUtil(long tenant_id){
        this.tenant_id=tenant_id;
    }
    @Override
    public Credential getRandomCredPair() {
        CredentialDao cdao = CredentialDao.getInstance();
        Credential c = cdao.getRandomCredential(tenant_id);
        if(c==null){
            return null;
        }
        return new Credential(c.getEmail(),c.getPassword());
    }
    @Override
    public String getRandomReciever() {
        CredentialDao cdao = CredentialDao.getInstance();
        String s = cdao.getRandomReciever(tenant_id);
        return s;
    }
}
