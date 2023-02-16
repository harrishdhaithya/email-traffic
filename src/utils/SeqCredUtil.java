package utils;

import java.util.Random;
import dao.TenantDao;
import model.Credential;
import model.Tenant;

public class SeqCredUtil implements ICredUtil {
    private String prefix;
    private String suffix;
    private int seqStart;
    private int seqEnd;
    private String password;
    private Tenant tenant;
    public SeqCredUtil(String prefix, String suffix, long tenant_id, int seqStart, int seqEnd, String password) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.seqStart = seqStart;
        this.seqEnd = seqEnd;
        this.password = password;
        TenantDao tdao = TenantDao.getInstance();
        this.tenant = tdao.getTenant(tenant_id);
    }
    @Override
    public Credential getRandomCredPair(){
        Random rand = new Random();
        int randInt = this.seqStart+rand.nextInt(this.seqEnd)+1;
        String email = this.prefix+randInt+((suffix!=null)?suffix:"")+"@"+this.tenant.getName();
        Credential cred = new Credential(email,this.password);
        return cred;
    }
    @Override
    public String getRandomReciever(){
        Random rand = new Random();
        int randInt = this.seqStart+rand.nextInt(this.seqEnd)+1;
        return this.prefix+randInt+((suffix!=null)?suffix:"")+"@"+this.tenant.getName();
    }
}
