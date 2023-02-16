package dao;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import model.MailTrace;

public class MailTraceDao {
    private static Logger logger = Logger.getLogger(MailTraceDao.class.getName());
    private static MailTraceDao mdao = null;
    public static MailTraceDao getInstance(){
        if(mdao==null){
            mdao = new MailTraceDao();
        }
        return mdao;
    }
    public long addTrace(long tenantid,String timestamp){
        DataObject dobj = new WritableDataObject();
        Row row = new Row("Traces");
        row.set("TENANT_ID", tenantid);
        row.set("TIMESTAMP", Timestamp.valueOf(LocalDateTime.now(ZoneId.of("UTC"))));
        row.set("RECENT_TRACE", Timestamp.valueOf(LocalDateTime.parse(timestamp)));
        row.set("STATUS", "RUNNING");
        long traceid = -1;
        try {
            dobj.addRow(row);
            DataAccess.add(dobj);
            traceid = row.getLong("ID");
        } catch (DataAccessException e) {
            logger.info(e.getMessage()+" for "+tenantid);
            e.printStackTrace();
        }
        return traceid;
    }

    public boolean updateTraceStatus(long traceid,String status){
        Criteria cri = new Criteria(new Column("Traces", "ID"), traceid, QueryConstants.EQUAL);
        try {
            UpdateQuery uq = new UpdateQueryImpl("Traces");
            uq.setCriteria(cri);
            uq.setUpdateColumn("STATUS", status);
            DataAccess.update(uq);
            return true;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Set<String> getMessageIDS(Timestamp startdate){
        Set<String> mids = new HashSet<>();
        if(startdate==null){
            startdate = Timestamp.valueOf(
                LocalDateTime.now(ZoneId.of("UTC")).minusHours(10)
            );
        }
        Criteria c = new Criteria(new Column("Traced_Mail", "TIMESTAMP"), startdate, QueryConstants.GREATER_EQUAL);
        SelectQuery sq = new SelectQueryImpl(new Table("Traced_Mail"));
        sq.addSelectColumn(new Column("Traced_Mail", "MESSAGE_ID"));
        sq.setCriteria(c);
        try {
            RelationalAPI relApi = RelationalAPI.getInstance();
            Connection conn = relApi.getConnection();
            DataSet ds = relApi.executeQuery(sq, conn);
            while(ds.next()){
                String mid = ds.getAsString("MESSAGE_ID");
                mids.add(mid);
            }
            conn.close();
        } catch (Exception e) {
            logger.warning("Not able to get messageIDS...");
            e.printStackTrace();
        }
        return mids;
    }

    // public void addRow(long traceid,MailTrace m,DataObject dobj) throws Exception{
    //     Row row = new Row("Traced_Mail");
    //     row.set("TRACE_ID", traceid);
    //     row.set("MESSAGE_ID",m.getMessageId());
    //     row.set("MESSAGE_TRACE_ID", m.getMessageTraceId());
    //     row.set("SENDER", m.getSender());
    //     row.set("RECEIVER", m.getReceiver());
    //     row.set("SUBJECT", m.getSubject());
    //     row.set("TIMESTAMP",Timestamp.valueOf(LocalDateTime.parse(m.getTimestamp())));
    //     row.set("STATUS",m.getStatus());
    //     dobj.addRow(row);
    // }


    public boolean addTrace(long traceid,MailTrace m){
        DataObject dobj = new WritableDataObject();
        Row row = new Row("Traced_Mail");
        row.set("TRACE_ID", traceid);
        row.set("MESSAGE_ID",m.getMessageId());
        row.set("MESSAGE_TRACE_ID", m.getMessageTraceId());
        row.set("SENDER", m.getSender());
        row.set("RECEIVER", m.getReceiver());
        row.set("SUBJECT", m.getSubject());
        row.set("TIMESTAMP",Timestamp.valueOf(LocalDateTime.parse(m.getTimestamp())));
        row.set("STATUS",m.getStatus());
        try {
            dobj.addRow(row);
            DataAccess.add(dobj);
            return true;
        } catch (DataAccessException e) {
            logger.info("Not able to insert row "+m.toString());
            logger.info(e.getMessage());
            // e.printStackTrace();
        }
        return false;
    }

    public Timestamp getRecentTrace(long tenantid){
        Criteria c = new Criteria(new Column("Traces","TENANT_ID"), tenantid, QueryConstants.EQUAL);
        Criteria c1 = new Criteria(new Column("Traces", "STATUS"), "SUCCESS", QueryConstants.EQUAL);
        SelectQuery sq = new SelectQueryImpl(new Table("Traces"));
        sq.addSelectColumn(new Column("Traces", "TIMESTAMP"));
        sq.setCriteria(c.and(c1));
        sq.addSortColumn(new SortColumn(new Column("Traces", "TIMESTAMP"), false));
        Range range = new Range(1, 1);
        sq.setRange(range);
        RelationalAPI relAPi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = relAPi.getConnection();
            ds = relAPi.executeQuery(sq, conn);
            if(ds.next()){
                Timestamp starttime = ds.getAsTimestamp("TRACE_END_TIME");
                conn.close();
                ds.close();
                return starttime;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }   

    public List<MailTrace> getMailTraces(long tenantid,int lowerbound,int count){
        List<MailTrace> mailTraces = new LinkedList<>();
        SelectQuery sq = new SelectQueryImpl(new Table("Traced_Mail"));
        sq.addJoin(new Join("Traced_Mail", "Traces", new String[]{"TRACE_ID"}, new String[]{"ID"}, Join.INNER_JOIN));
        sq.addSelectColumn(new Column("Traced_Mail", "*"));
        sq.addSelectColumn(new Column("Traces","TENANT_ID"));
        sq.addSortColumn(new SortColumn(new Column("Traced_mail", "TIMESTAMP"), false));
        Range range = new Range(lowerbound, count);
        sq.setRange(range);
        Criteria c = new Criteria(new Column("Traces", "TENANT_ID"), tenantid, QueryConstants.EQUAL);
        sq.setCriteria(c);
        Connection conn = null;
        RelationalAPI relApi = RelationalAPI.getInstance();
        try{
            conn = relApi.getConnection();
            DataSet ds = relApi.executeQuery(sq, conn);
            while(ds.next()){
                long id = ds.getAsLong("ID");
                long traceid = ds.getAsLong("TRACE_ID");
                String messageId = ds.getAsString("MESSAGE_ID");
                String messageTraceId = ds.getAsString("MESSAGE_TRACE_ID");
                String sender = ds.getAsString("SENDER");
                String receiver = ds.getAsString("RECEIVER");
                String subject = ds.getAsString("SUBJECT");
                String timestamp = ds.getAsTimestamp("TIMESTAMP").toLocalDateTime().plusHours(5).plusMinutes(30).toString();
                String status = ds.getAsString("STATUS");
                MailTrace m = new MailTrace(id, traceid, sender, receiver, subject, timestamp, messageId, messageTraceId,status,tenantid);
                mailTraces.add(m);
            }
            ds.close();
            conn.close();

        }catch(Exception ex){
            logger.warning(ex.getMessage());
            ex.printStackTrace();
        }
        return mailTraces;
    }

    public void addMessageTraces(List<MailTrace> traces,long traceid) throws Exception{
        DataObject dobj = new WritableDataObject();
        for(MailTrace m:traces){
            Row row = new Row("Traced_Mail");
            row.set("TRACE_ID", traceid);
            row.set("MESSAGE_ID",m.getMessageId());
            row.set("MESSAGE_TRACE_ID", m.getMessageTraceId());
            row.set("SENDER", m.getSender());
            row.set("RECEIVER", m.getReceiver());
            row.set("SUBJECT", m.getSubject());
            row.set("TIMESTAMP",Timestamp.valueOf(LocalDateTime.parse(m.getTimestamp())));
            row.set("STATUS",m.getStatus());
            dobj.addRow(row);
        }
        DataAccess.add(dobj);
    }
    public long getMailTraceCount(long tenantid){
        SelectQuery sq = new SelectQueryImpl(new Table("Traced_Mail"));
        sq.addJoin(new Join("Traced_Mail", "Traces", new String[]{"TRACE_ID"}, new String[]{"ID"}, Join.INNER_JOIN));
        Criteria c = new Criteria(new Column("Traces","TENANT_ID"), tenantid, QueryConstants.EQUAL);
        Column id = new Column("Traced_Mail", "ID");
        Column countCol = id.count();
        countCol.setColumnAlias("COUNT");
        sq.addSelectColumn(countCol);
        sq.setCriteria(c);
        try {
            RelationalAPI relApi = RelationalAPI.getInstance();
            Connection conn = relApi.getConnection();
            DataSet ds = relApi.executeQuery(sq,conn);
            while (ds.next()) {
                long count = ds.getAsLong("COUNT");
                ds.close();
                conn.close();
                return count;
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    public JSONArray getTraceStatusByTenant(long tenantid){
        JSONArray traces = new JSONArray();
        Criteria c = new Criteria(new Column("Traces", "TENANT_ID"), tenantid, QueryConstants.EQUAL);
        SelectQuery sq = new SelectQueryImpl(new Table("Traces"));
        sq.setCriteria(c);
        sq.addSortColumn(new SortColumn(new Column("Traces", "TIMESTAMP"), false));
        sq.addSelectColumn(new Column("Traces", "*"));
        TenantDao tdao = TenantDao.getInstance();
        try {
            DataObject dobj = DataAccess.get(sq);
            Iterator itr = dobj.getRows("Traces");
            while (itr.hasNext()) {
                JSONObject jobj = new JSONObject();
                Row row = (Row)itr.next();
                jobj.put("id", row.getLong("ID"));
                jobj.put("tracestart", row.getTimestamp("TIMESTAMP").toLocalDateTime().plusHours(5).plusMinutes(30).toString());
                jobj.put("recentTrace", row.getTimestamp("RECENT_TRACE").toLocalDateTime().plusHours(5).plusMinutes(30).toString());
                jobj.put("tenant", tdao.getTenant(row.getLong("TENANT_ID")).getName());
                jobj.put("status", row.getString("STATUS"));
                traces.put(jobj);
            } 
        } catch (DataAccessException e) {
            logger.warning("Not able to Get Trace History...");
            e.printStackTrace();
        }
        return traces;
    }
    public JSONArray getAllTraces(){
        JSONArray traces = new JSONArray();
        try{
            SelectQuery sq = new SelectQueryImpl(new Table("Traces"));
            sq.addSortColumn(new SortColumn(new Column("Traces","TIMESTAMP"), false));
            sq.addSelectColumn(new Column("Traces","*"));
            DataObject dobj = DataAccess.get(sq);
            Iterator itr = dobj.getRows("Traces");
            TenantDao tdao = TenantDao.getInstance();
            while (itr.hasNext()) {
                JSONObject jobj = new JSONObject();
                Row row = (Row)itr.next();
                jobj.put("id", row.getLong("ID"));
                jobj.put("tracestart", row.getTimestamp("TIMESTAMP").toLocalDateTime().plusHours(5).plusMinutes(30).toString());
                jobj.put("recentTrace", row.getTimestamp("RECENT_TRACE").toLocalDateTime().plusHours(5).plusMinutes(30).toString());
                jobj.put("tenant", tdao.getTenant(row.getLong("TENANT_ID")).getName());
                jobj.put("status", row.getString("STATUS"));
                traces.put(jobj);
            } 
        }catch(DataAccessException e){
            logger.warning("Not able to Get Trace History...");
            e.printStackTrace();
        }
        return traces;
    }
}
