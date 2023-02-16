package controller;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
import dao.TrafficHistoryDao;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import model.Config;
import model.Credential;
import model.Mail;
import model.TrafficHistory;
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
    private String fileContent;
    private int seqStart;
    private int seqEnd;
    private String prefix;
    private String suffix;
    private String password;
    private int countMail;
    private boolean completed=false;
    private ExecutorService es;
    private long tenant_id;
    private long starttime;
    private long endTime;
    private Timestamp startTimeStamp;
    private Timestamp endTimestamp;
    private static Logger logger = Logger.getLogger(MailTrafficGenerator.class.getName());

    public MailTrafficGenerator(int count,String fileContent,long tenant_id){
        ConfigDao cdao = ConfigDao.getInstance();
        Config c = cdao.getConfig("poolsize");
        this.count=count;
        this.countMail=count;
        this.poolSize = (c!=null)?Integer.parseInt(c.getPropValue()):25;
        this.mailCount=count;
        this.fileContent=fileContent;
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
    private ICredUtil getCredUtil() throws Exception{
        if(dataSource.equals("csv")){
            System.out.println("File Content: "+fileContent);
            return new CSVCredUtil(this.fileContent);
        }else if(dataSource.equals("sequence")){
            if(seqStart>seqEnd){
                throw new Exception("Invalid Interval");
            }
            if(password==null){
                throw new Exception("The value of the password cannot be null");
            }
            return new SeqCredUtil(prefix, suffix, tenant_id, seqStart, seqEnd, password);
        }else{
            return new DBCredUtil(this.tenant_id);
        }
    }
    private boolean sendEmail(ExchangeService eserv,ExchangeCredentials ecred, Mail mail){
        try {
            eserv.setUrl(new URI(serviceUrl));
            logger.info("Inside sendEmail");
            eserv.setCredentials(ecred);
            System.out.println("Completed Setting credentials...");
            eserv.setTraceEnabled(true);
            EmailMessage message = new EmailMessage(eserv);
            message.setSubject(mail.getSubject());
            message.setBody(MessageBody.getMessageBodyFromText(mail.getContent()));
            logger.info("Message: "+message.toString());
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
    public void generateTraffic() throws Exception{
        starttime = System.currentTimeMillis();
        startTimeStamp = Timestamp.valueOf(LocalDateTime.now());
        List<Future<?>> futures = new ArrayList<>();
        ICredUtil credUtil = getCredUtil();
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
                    CredentialProvider provider = CredentialProvider.getInstance();
                    ExchangeCredentials exc;
                    try {
                        exc = provider.getCredential(cred.getEmail(), cred.getPassword(),this.tenant_id);
                    } catch (Exception e) {
                        logger.warning(e.toString());
                        e.printStackTrace();
                        synchronized(this){
                            this.countMail--;
                            this.failureCount++;                            
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
                    logger.info(toList.toString());
                    Faker faker = new Faker();
                    String subject = faker.lorem().paragraph();
                    String body = faker.lorem().paragraph();
                    String fromEmail = cred.getEmail();
                    Mail mail = new Mail(fromEmail, toList, subject, body);
                    boolean success = sendEmail(eserv,exc, mail);
                    logger.info("Mail Sent form "+fromEmail+"to "+toList.toString());
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
                logger.info("Status: "+getStatus());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.es.shutdown();
            logger.info("Completed generating traffic...");
            logger.info(getStatus());
            endTime = System.currentTimeMillis();
            endTimestamp = Timestamp.valueOf(LocalDateTime.now());
            completed=true;
            TrafficHistory th = new TrafficHistory(tenant_id, successCount, failureCount, mailCount, startTimeStamp, endTimestamp);
            TrafficHistoryDao thdao = TrafficHistoryDao.getInstance();
            thdao.addTrafficHistory(th);
            Thread.currentThread().interrupt();
        };
        Thread t = new Thread(r);
        t.start();
    }
    public String getStatus(){
        JSONObject jobj = new JSONObject();
        jobj.put("success", this.successCount);
        jobj.put("failure", this.failureCount);
        jobj.put("starttime", startTimeStamp.toString());
        logger.info("Start Time: "+this.starttime/1000);
        logger.info("End Time: "+this.endTime/1000);
        logger.info("Count: "+this.count);
        if(this.completed){
            jobj.put("timetaken", getTimeTaken());
            jobj.put("endtime", endTimestamp.toString());
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
        return this.completed;
    }
    private long getTimeTaken(){
        return (endTime-starttime)/1000;
    }
}