package dao;

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import model.TrafficHistory;

public class TrafficHistoryDao {
    private static Logger logger = Logger.getLogger(TrafficHistoryDao.class.getName());
    private static TrafficHistoryDao thdao = null;
    public static TrafficHistoryDao getInstance(){
        if(thdao==null){
            thdao = new TrafficHistoryDao();
        }
        return thdao;
    }
    public boolean addTrafficHistory(TrafficHistory th){
        DataObject dobj = new WritableDataObject();
        Row row = new Row("Traffic_History");
        row.set("TENANT_ID", th.getTenantid());
        row.set("SUCCESS_COUNT", th.getSuccessCount());
        row.set("FAILURE_COUNT",th.getFailureCount());
        row.set("TOTAL_COUNT", th.getTotalCount());
        row.set("START_TIME", th.getStarttime());
        row.set("END_TIME",th.getEndtime());
        try {
            dobj.addRow(row);
            DataAccess.add(dobj);
            return true;
        } catch (DataAccessException e) {
            logger.warning("Not able to insert log...");
            e.printStackTrace();
        }
        return false;
    }
    
    public List<TrafficHistory> getAllTrafficHistory(){
        List<TrafficHistory> hist = new LinkedList<>();
        try {
            DataObject dobj = DataAccess.get("Traffic_History", (Criteria)null);
            Iterator itr = dobj.getRows("Traffic_History");
            while (itr.hasNext()) {
                Row row = (Row)itr.next();
                long id = row.getLong("ID");
                long tenantid = row.getLong("TENANT_ID");
                long successCount = row.getLong("SUCCESS_COUNT");
                long failureCount = row.getLong("FAILURE_COUNT");
                long totalCount = row.getLong("TOTAL_COUNT");
                Timestamp startTime = row.getTimestamp("START_TIME");
                Timestamp endTime = row.getTimestamp("END_TIME");
                TrafficHistory th = new TrafficHistory(id, tenantid, successCount, failureCount, totalCount, startTime, endTime);
                hist.add(th);
            }
        } catch (DataAccessException e) {
            logger.warning("Not able to get traffic history...");
            e.printStackTrace();
        }
        return hist;
    }

    public List<TrafficHistory> getTrafficHistories(long tenantid){
        List<TrafficHistory> hist = new LinkedList<>();
        Criteria c = new Criteria(new Column("Traffic_History","TENANT_ID"), tenantid, QueryConstants.EQUAL);
        try{
            DataObject dobj = DataAccess.get("Traffic_History", c);
            Iterator itr = dobj.getRows("Traffic_History");
            while (itr.hasNext()) {
                Row row = (Row)itr.next();
                long id = row.getLong("ID");
                long successCount = row.getLong("SUCCESS_COUNT");
                long failureCount = row.getLong("FAILURE_COUNT");
                long totalCount = row.getLong("TOTAL_COUNT");
                Timestamp starttime = row.getTimestamp("START_TIME");
                Timestamp endtime = row.getTimestamp("END_TIME");
                TrafficHistory th = new TrafficHistory(id, tenantid, successCount, failureCount, totalCount, starttime, endtime);
                hist.add(th);
            }
        }catch(DataAccessException e){
            logger.warning("Not able to get traffic");
            e.printStackTrace();
        }
        return hist;
    }
}
