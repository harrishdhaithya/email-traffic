package messagetrace;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import auth.CredentialProvider;
import dao.MailTraceDao;
import model.MailTrace;

public class MessageTrace {
    private static Logger logger = Logger.getLogger(MessageTrace.class.getName());
    private static final String API_URL = "https://reports.office365.com/ecp/reportingwebservice/reporting.svc/MessageTrace";
    private static final String SCOPE_URL = "https://outlook.office365.com/.default";
    private long mailTraceCount = 0;
    private long skiptoken = 0;
    private String status = "RUNNING";
    private Timestamp startTime = null;
    private Timestamp endTime = null;
    private long traceid;


    private static String constructFilter(Timestamp startDate) throws Exception{
        StringBuilder sb = new StringBuilder();
        LocalDateTime startDateLocale = null;
        if(startDate==null||startDate.toLocalDateTime().isBefore(LocalDateTime.now(ZoneOffset.UTC).minusHours(10))){
            startDateLocale = LocalDateTime.now(ZoneOffset.UTC).minusHours(10);
        }else{
            startDateLocale = startDate.toLocalDateTime().atZone(ZoneId.of("UTC")).toLocalDateTime()
            .plusSeconds(1);
            logger.info(startDateLocale.toString());
        }
        sb.append("StartDate eq datetime'");
        sb.append(startDateLocale.toString());
        sb.append("'");
        sb.append(" and ");
        sb.append("EndDate eq datetime'");
        sb.append(LocalDateTime.now(ZoneOffset.UTC).toString());
        sb.append("'");
        return sb.toString();
    }


    private boolean update(long tenantid,Timestamp startdate,Set<String> insertedVals) throws Exception{
        MailTraceDao mtdao = MailTraceDao.getInstance();
        List<MailTrace> traces = new LinkedList<>(); 
        CredentialProvider cred = CredentialProvider.getInstance();
        URL reqUrl;
        Set<String> scopes = new HashSet<>();
        scopes.add(SCOPE_URL);
        try{
            String token = cred.getAdminToken(tenantid, scopes);
            String filter = constructFilter(startdate);
            logger.info(filter);
            String param = "$filter="+URLEncoder.encode(filter, "UTF-8")+"&$skiptoken="+URLEncoder.encode(Long.toString(skiptoken), "UTF-8");
            reqUrl = new URL(API_URL+"?"+param);
            HttpURLConnection conn = (HttpURLConnection)reqUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Bearer "+token);
            conn.setRequestProperty("Accept", "application/json");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            StringBuilder sb = new StringBuilder();
            while((line=br.readLine())!=null){
                sb.append(line);
            }
            JSONObject jobj = new JSONObject(sb.toString());
            JSONArray jarr = jobj.getJSONArray("value");
            for(int i=0;i<jarr.length();i++){
                JSONObject trace = jarr.getJSONObject(i);
                System.out.println(trace);
                String sender = trace.getString("SenderAddress");
                String receiver = trace.getString("RecipientAddress");
                String subject = trace.get("Subject").toString();
                String timestamp = trace.getString("Received");
                String messageId = trace.getString("MessageId");
                String messageTraceId = trace.getString("MessageTraceId");
                String status = trace.getString("Status");
                if(this.skiptoken==0&&i==0){
                    traceid = mtdao.addTrace(tenantid,timestamp);
                    if(traceid==-1){
                        throw new Exception("Unable to add Trace...");
                    }
                }
                if(insertedVals.contains(messageId)){
                    logger.info("The messageId "+messageId+" Already Exist.");
                    continue;
                }
                MailTrace mt = new MailTrace(tenantid, sender, receiver, subject, timestamp,messageId,messageTraceId,status);
                traces.add(mt);
                mailTraceCount++;
            }
            mtdao.addMessageTraces(traces, tenantid);
            if(jobj.has("odata.nextLink")){
                String nextLink = jobj.getString("odata.nextLink");
                System.out.println(nextLink);
                int index = nextLink.indexOf("$skiptoken");
                String nextToken = nextLink.substring(index+11);
                logger.info("Next Link: "+nextLink);
                skiptoken = Integer.parseInt(nextToken);
            }else{
                return false;
            }
        }catch(Exception ex){
            logger.warning(ex.getMessage());
            ex.printStackTrace();
            mtdao.updateTraceStatus(traceid, false);
            throw new Exception("Something went wrong...");
        }
        return true;
    }

    public void updateTraceAsync(long tenantid){
        Thread th = new Thread(()->{
            startTime = Timestamp.valueOf(LocalDateTime.now());
            try {
                updateTraces(tenantid);
            } catch (Exception e) {
                e.printStackTrace();
                status = "FAILED";
            }
            Thread.currentThread().interrupt();
            endTime = Timestamp.valueOf(LocalDateTime.now());
        });
        th.start();
    }
    
    public void updateTraces(long tenantid) throws Exception{
        MailTraceDao mtdao = MailTraceDao.getInstance();
        Timestamp startdate = mtdao.getRecentTrace(tenantid);
        logger.info("Start Date: "+startdate);
        // long traceid = mtdao.addTrace(tenantid);
        boolean cont = true;
        Set<String> mids = mtdao.getMessageIDS(startdate);
        while(cont){
            cont = this.update(tenantid, startdate,mids);
        }
        mtdao.updateTraceStatus(traceid, true);
        status = "COMPLETED";
    }

    public JSONObject getStatus(){
        JSONObject jobj = new JSONObject();
        jobj.put( "mailcount", mailTraceCount);
        jobj.put("status", status);
        jobj.put("starttime",startTime);
        if(status.equals("COMPLETED")||status.equals("FAILED")){
            jobj.put("endtime",endTime);
        }
        return jobj;
    }
}
