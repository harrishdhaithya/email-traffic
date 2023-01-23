package controller;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.github.javafaker.Faker;
import auth.CredentialProvider;
import dao.ConfigDao;
import exception.CredentialException;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import model.Config;
import model.Credential;
import model.Mail;
import utils.CSVCredUtil;
import utils.DBCredUtil;
import utils.ICredUtil;
import utils.SeqCredUtil;

public class MailTrafficGenerator {
    private static String serviceUrl = "https://outlook.office365.com/ews/Exchange.asmx";
    private int successCount = 0;
    private int failureCount = 0;
    private int count;
    private final int mailCount;
    private int poolSize;
    private String dataSource;
    private String filepath;
    private int seqStart;
    private int seqEnd;
    private String prefix;
    private String suffix;
    private String password;
    private int countMail;
    private ExecutorService es;
    private long tenant_id;
    private long starttime;
    private long endTime;
    Logger logger = Logger.getLogger(this.getClass().getName());
    public MailTrafficGenerator(int count,String filepath,long tenant_id){
        ConfigDao cdao = ConfigDao.getInstance();
        Config c = cdao.getConfig("poolsize");
        this.count=count;
        this.countMail=count;
        this.poolSize = (c!=null)?Integer.parseInt(c.getPropValue()):25;
        this.mailCount=count;
        this.filepath=filepath;
        this.dataSource="csv";
        this.tenant_id=tenant_id;
        this.es = Executors.newFixedThreadPool(poolSize);
    }
    public MailTrafficGenerator(int count, int seqStart,int seqEnd, String prefix,String suffix,String password,long tenant_id){
        ConfigDao cdao = ConfigDao.getInstance();
        Config c = cdao.getConfig("poolsize");
        this.count=count;
        this.countMail=count;
        this.poolSize = (c!=null)?Integer.parseInt(c.getPropValue()):25;
        this.mailCount=count;
        this.seqStart = seqStart;
        this.seqEnd = seqEnd;
        this.prefix = prefix;
        this.suffix = suffix;
        this.password = password;
        this.dataSource = "sequence";
        this.tenant_id = tenant_id;
        this.es = Executors.newFixedThreadPool(poolSize);
    }
    public MailTrafficGenerator(long tenant_id,int count){
        ConfigDao cdao = ConfigDao.getInstance();
        Config c = cdao.getConfig("poolsize");
        this.poolSize = (c!=null)?Integer.parseInt(c.getPropValue()):25;
        this.tenant_id = tenant_id;
        this.count = count;
        this.mailCount = count;
        this.countMail = count;
        this.dataSource = "db";
        this.es = Executors.newFixedThreadPool(poolSize);
    }
    private ICredUtil getCredUtil() throws CredentialException{
        if(dataSource.equals("csv")){
            File f = new File(this.filepath);
            if(!f.exists()){
                throw new CredentialException("FILE NOT FOUND...");
            }
            return new CSVCredUtil(this.filepath);
        }else if(dataSource.equals("sequence")){
            if(seqStart>seqEnd){
                throw new CredentialException("Invalid Interval");
            }
            if(password==null){
                throw new CredentialException("The value of the password cannot be null");
            }
            return new SeqCredUtil(prefix, suffix, tenant_id, seqStart, seqEnd, password);
        }else{
            return new DBCredUtil(this.tenant_id);
        }
    }
    private boolean sendEmail(ExchangeService eserv,ExchangeCredentials ecred, Mail mail){
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
    private synchronized void incSuccessCount(){
        this.successCount++;
    }
    private synchronized void incFailureCount(){
        this.failureCount++;
    }
    public void generateTraffic() throws CredentialException{
        starttime = System.currentTimeMillis();
        List<Future<?>> futures = new ArrayList<>();
        ICredUtil credUtil = getCredUtil();
        System.out.println(credUtil);
        for(int i=0;i<poolSize;i++){
            ExchangeService eserv = new ExchangeService();
            Runnable r = () -> {
                while(true){
                    synchronized(this){
                        if(this.count<=0){
                            break;
                        }
                        this.count--;
                    }
                    Credential cred=credUtil.getRandomCredPair();
                    System.out.println(cred.getEmail());
                    System.out.println(cred.getPassword());
                    CredentialProvider provider = CredentialProvider.getInstance();
                    ExchangeCredentials exc;
                    try {
                        exc = provider.getCredential(cred.getEmail(), cred.getPassword(),this.tenant_id);
                    } catch (CredentialException e) {
                        e.printStackTrace();
                        synchronized(this){
                            this.countMail--;
                            this.failureCount++;
                            logger.warning(e.toString());
                        }
                        continue;
                    } 
                    Set<String> toList = new HashSet<>();
                    Random rand = new Random();
                    int toCount = rand.nextInt(10)+1;
                    for(int j=0;j<toCount;j++){
                    String reciever = credUtil.getRandomReciever();
                    toList.add(reciever);
                    }
                    Faker faker = new Faker();
                    String subject = faker.lorem().paragraph();
                    String body = faker.lorem().paragraph();
                    String fromEmail = cred.getEmail();
                    Mail mail = new Mail(fromEmail, toList, subject, body);
                    boolean success = sendEmail(eserv,exc, mail);
                    mail.setStatus(success);
                    if(success){
                        incSuccessCount();
                    }else{
                        incFailureCount();
                    }
                    synchronized(this){
                        this.countMail--;
                    }
                }
            };
            Future<?> f = es.submit(r);
            futures.add(f);
        } 
        handleThread();
    }
    private void handleThread(){
        Runnable r = ()->{
            while(this.countMail!=0){
                System.out.println("Status: "+getStatus());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            endTime = System.currentTimeMillis();
            this.es.shutdown();
            Thread.currentThread().interrupt();
            logger.info("Completed...");
            logger.info(getStatus());
        };
        Thread t = new Thread(r);
        t.start();
    }
    public String getStatus(){
        JSONObject jobj = new JSONObject();
        jobj.put("success", this.successCount);
        jobj.put("failure", this.failureCount);
        logger.info("Start Time: "+this.starttime/1000);
        logger.info("End Time: "+this.endTime/1000);
        logger.info("Count: "+this.count);
        if(this.countMail==0){
            jobj.put("timetaken", getTimeTaken());
        }
        jobj.put("pending",this.mailCount-(this.successCount+this.failureCount));
        return jobj.toString();
    }
    public int getSuccessCount(){
        return this.successCount;
    }
    public int getFailureCount(){
        return this.failureCount;
    }
    public int getCount(){
        return this.count;
    }
    public boolean isCompleted(){
        return this.countMail==0;
    }
    private long getTimeTaken(){
        return (endTime-starttime)/1000;
    }
}