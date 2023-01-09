package utils;

import java.util.Random;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;

public class SeqCredUtil implements ICredUtil {
    private String prefix;
    private String suffix;
    private String tenant;
    private int seqStart;
    private int seqEnd;
    private String password;
    public SeqCredUtil(String prefix, String suffix, String tenant, int seqStart, int seqEnd, String password) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.tenant = tenant;
        this.seqStart = seqStart;
        this.seqEnd = seqEnd;
        this.password = password;
    }
    @Override
    public  ExchangeCredentials getRandomCredPair(){
        Random rand = new Random();
        int randInt = this.seqStart+rand.nextInt(this.seqEnd)+1;
        String email = this.prefix+randInt+((suffix!=null)?suffix:"")+"@"+this.tenant;
        ExchangeCredentials cred = new WebCredentials(email,this.password);
        return cred;
    }
    @Override
    public String getRandomReciever(){
        Random rand = new Random();
        int randInt = this.seqStart+rand.nextInt(this.seqEnd)+1;
        return this.prefix+randInt+((suffix!=null)?suffix:"")+"@"+this.tenant;
    }
}
