package dao;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
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
        row.set("TIMESTAMP", Timestamp.valueOf(LocalDateTime.parse(timestamp)));
        row.set("SUCCESS", "true");
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

    public boolean updateTraceStatus(long traceid,boolean status){
        Criteria cri = new Criteria(new Column("Traces", "ID"), traceid, QueryConstants.EQUAL);
        try {
            UpdateQuery uq = new UpdateQueryImpl("Traces");
            uq.setCriteria(cri);
            uq.setUpdateColumn("SUCCESS", Boolean.toString(status));
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
            return mids;
        }
        Timestamp minus10 = Timestamp.valueOf(startdate.toLocalDateTime()
                            .minusHours(10)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDateTime());
        logger.info("Timestamp: "+minus10.toString());
        if(startdate.before(minus10)){
            return mids;
        }
        Criteria c = new Criteria(new Column("Traced_Mail", "TIMESTAMP"), minus10, QueryConstants.GREATER_EQUAL);
        SelectQuery sq = new SelectQueryImpl(new Table("Traced_Mail"));
        sq.addSelectColumn(new Column("Traced_Mail", "MESSAGE_ID"));
        sq.setCriteria(c);
        try {
            RelationalAPI relApi = RelationalAPI.getInstance();
            Connection conn = relApi.getConnection();
            DataSet ds = relApi.executeQuery(sq, conn);
            if(ds.next()){
                String mid = ds.getAsString("MESSAGE_ID");
                mids.add(mid);
            }
        } catch (Exception e) {
            logger.warning("Not able to get messageIDS...");
            e.printStackTrace();
        }
        logger.info("Message IDs: "+mids.toString());
        return mids;
    }

    public void addRow(long traceid,MailTrace m,DataObject dobj) throws Exception{
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

    public void saveDobj(DataObject dobj) throws Exception{
        System.out.println(dobj);
        DataAccess.add(dobj);
    }

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
        Criteria c1 = new Criteria(new Column("Traces", "SUCCESS"), "true", QueryConstants.EQUAL);
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
                Timestamp timestamp = ds.getAsTimestamp("TIMESTAMP");
                logger.info("Timestamp: "+timestamp);
                conn.close();
                ds.close();
                return timestamp;
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
                String timestamp = ds.getAsString("TIMESTAMP");
                String status = ds.getAsString("STATUS");
                MailTrace m = new MailTrace(id, traceid, sender, receiver, subject, timestamp, messageId, messageTraceId,status);
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
}
