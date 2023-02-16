package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.json.JSONObject;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.Range;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import model.Credential;

public class CredentialDao {
    private static Logger logger = Logger.getLogger(CredentialDao.class.getName());
    private static CredentialDao cdao = null;
    public static CredentialDao getInstance(){
        if(cdao==null){
            cdao = new CredentialDao();
        }
        return cdao;
    }

    public boolean addCredentials(List<Credential> credentials){
        DataObject dobj = new WritableDataObject();
        try {
            Persistence per = (Persistence)BeanUtil.lookup("Persistence");
            for(int i=1;i<credentials.size();i++){
                Credential cred = credentials.get(i);
                Row row = new Row("Credentials");
                
                row.set("EMAIL", cred.getEmail());
                row.set("PASSWORD", cred.getPassword());
                row.set("TENANT_ID", cred.getTenantId());
                dobj.addRow(row);
            }
            per.add(dobj);
            return true;
        } catch (Exception e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    public long getCredCount(long tenantid){
        SelectQuery sq = new SelectQueryImpl(new Table("Credentials"));
        Criteria c = new Criteria(new Column("Credentials","TENANT_ID"), tenantid, QueryConstants.EQUAL);
        Criteria pwdC = new Criteria(new Column("Credentials","PASSWORD"), null, QueryConstants.NOT_EQUAL);
        Column id = new Column("Credentials", "ID");
        Column countCol = id.count();
        countCol.setColumnAlias("COUNT");
        sq.addSelectColumn(countCol);
        sq.setCriteria(c.and(pwdC));
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
    public List<Credential> getCredentials(long tenantId){
        List<Credential> creds = new LinkedList<>();
        try {
            Criteria c = new Criteria(new Column("Credentials","TENANT_ID"), tenantId, QueryConstants.EQUAL);
            Criteria pwdC = new Criteria(new Column("Credentials", "PASSWORD"), null, QueryConstants.NOT_EQUAL);
            Criteria statusC = new Criteria(new Column("Credentials", "STATUS"), "ACTIVE", QueryConstants.EQUAL);
            DataObject dobj = DataAccess.get("Credentials", c.and(pwdC).and(statusC));
            Iterator<?> itr = dobj.getRows("Credentials");
            while(itr.hasNext()){
                Row row = (Row)itr.next();
                long id = row.getLong("ID");
                String email = row.getString("EMAIL");
                String password = row.getString("PASSWORD");
                String status = row.getString("STATUS");
                Credential cred = new Credential(id, email, password, tenantId,status);
                creds.add(cred);
            }
            return creds;
        } catch (DataAccessException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public List<Credential> getCredentials(long tenantid,int lowerBound,int count){
        List<Credential> creds = new LinkedList<>();
        Criteria c = new Criteria(new Column("Credentials", "PASSWORD"), null, QueryConstants.NOT_EQUAL);
        Criteria c2 = new Criteria(new Column("Credentials", "TENANT_ID"), tenantid, QueryConstants.EQUAL);
        SelectQuery sq = new SelectQueryImpl(new Table("Credentials"));
        sq.addSelectColumn(new Column("Credentials", "*"));
        sq.addSortColumn(new SortColumn(new Column("Credentials","ID"), true));
        Range range = new Range(lowerBound, count);
        sq.setRange(range);
        sq.setCriteria(c.and(c2));
        try {
            DataObject dobj = DataAccess.get(sq);
            Iterator itr = dobj.getRows("Credentials");
            while(itr.hasNext()){
                Row row = (Row)itr.next();
                long id = row.getLong("ID");
                String email = row.getString("EMAIL");
                String password = row.getString("PASSWORD");
                String status = row.getString("STATUS");
                Credential cred = new Credential(id, email, password, id,status);
                creds.add(cred);
            }
        } catch (DataAccessException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return creds;
    }
    public Credential getCredential(long id){
        Criteria crie = new Criteria(new Column("Credentials", "ID"), id, QueryConstants.EQUAL);
        try {
            DataObject dobj = DataAccess.get("Credentials", crie);
            Iterator itr = dobj.getRows("Credentials");
            if(itr.hasNext()){
                Row row = (Row)itr.next();
                String email = row.getString("EMAIL");
                String password = row.getString("PASSWORD");
                String status = row.getString("STATUS");
                long tenantid = row.getLong("TENANT_ID");
                Credential cred = new Credential(id, email, password, tenantid, status);
                return cred;
            }
        } catch (DataAccessException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    public JSONObject getSenderAndRecCount(long tenant_id){
        Criteria c1 = new Criteria(new Column("Credentials","PASSWORD"),null , QueryConstants.NOT_EQUAL);
        Criteria c2 = new Criteria(new Column("Credentials", "PASSWORD"),null,QueryConstants.EQUAL);
        Criteria tenant = new Criteria(new Column("Credentials", "TENANT_ID"), tenant_id,QueryConstants.EQUAL);
        Criteria active = new Criteria(new Column("Credentials","STATUS"),"ACTIVE",QueryConstants.EQUAL);
        JSONObject jobj = null;
        try {
            jobj = new JSONObject();
            DataObject dobj1 = DataAccess.get("Credentials",c1.and(tenant).and(active));
            DataObject dobj2 = DataAccess.get("Credentials", c2.and(tenant).and(active));
            int size1 = dobj1.size("Credentials");
            int size2 = dobj2.size("Credentials");
            jobj.put("sender", (size1==-1)?0:size1);
            jobj.put("receiver", (size2==-1)?0:size2);
            return jobj;
        } catch (DataAccessException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Credential getRandomCredential(long tenantid){
        RelationalAPI relApi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        Credential c = null;
        try {
            conn = relApi.getConnection();
            ds=relApi.executeQuery(conn, "select * from credentials c where tenant_id="+tenantid+" and password IS NOT NULL and STATUS='ACTIVE' order by random() limit 1");
            if(ds.next()){
                long id = ds.getAsLong("ID");
                String email = ds.getAsString("EMAIL").trim();
                String password = ds.getAsString("PASSWORD").trim();
                long tenantId = ds.getAsLong("TENANT_ID");
                String status = ds.getAsString("STATUS");
                c = new Credential(id,email, password, tenantId,status);
            }
            conn.close();
            ds.close();
        } catch (SQLException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return c;
    }
    
    public String getRandomReciever(long tenant_id){
        RelationalAPI relApi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        String email = null;
        try {
            conn = relApi.getConnection();
            ds=relApi.executeQuery(conn, "select EMAIL from credentials c where tenant_id="+tenant_id+" and STATUS='ACTIVE' order by random() limit 1 ");
            if(ds.next()){
                email = ds.getAsString("EMAIL");
            }
            conn.close();
            ds.close();
            return email;
        } catch (SQLException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public String[] getNRandomEmails(long tenantid,long n){
        String[] emails = new String[(int)n];
        RelationalAPI relApi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        int i=0;
        try{
            conn = relApi.getConnection();
            ds = relApi.executeQuery(conn, "select EMAIL from credentials where tenant_id="+tenantid+" and password IS NOT NULL and STATUS='ACTIVE' order by random() limit "+n);
            while(ds.next()){
                String email = ds.getAsString("EMAIL");
                emails[i++] = email;
            }
            ds.close();
            conn.close();
            if(i==0){
                return null;
            }
        }catch(SQLException ex){
            logger.warning(ex.getMessage());
            ex.printStackTrace();
        }
        return emails;
    }

    public boolean deleteMailboxes(String[] emails){
        Criteria c = new Criteria(new Column("Credentials","EMAIL"), emails, QueryConstants.IN);
        UpdateQuery uq = new UpdateQueryImpl("Credentials");
        uq.setCriteria(c);
        uq.setUpdateColumn("STATUS", "DELETED");
        uq.setUpdateColumn("DELETED_TIME", Timestamp.valueOf(LocalDateTime.now()));
        try {
            DataAccess.update(uq);
            return true;
        } catch (DataAccessException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCred(long id){
        Criteria c = new Criteria(new Column("Credentials", "ID"), id, QueryConstants.EQUAL);
        try {
            DataAccess.delete(c);
            return true;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean inactivateNMailBoxes(long tenantid,long n){
        String[] emails = getNRandomEmails(tenantid, n);
        Criteria c1 = new Criteria(new Column("Credentials", "TENANT_ID"),tenantid , QueryConstants.EQUAL);
        Criteria c2 = new Criteria(new Column("Credentials" , "EMAIL"), emails, QueryConstants.IN);
        UpdateQuery uq = new UpdateQueryImpl("Credentials");
        uq.setCriteria(c1.and(c2));
        uq.setUpdateColumn("STATUS","INACTIVE");
        uq.setUpdateColumn("INACTIVATED_TIME", Timestamp.valueOf(LocalDateTime.now()));
        try{
            DataAccess.update(uq);
            return true;
        } catch (DataAccessException e){
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCredentials(String email,String password){
        Criteria cri = new Criteria(new Column("Credentials", "EMAIL"), email, QueryConstants.EQUAL);
        try {
            UpdateQuery uq = new UpdateQueryImpl("Credentials");
            uq.setCriteria(cri);
            uq.setUpdateColumn("PASSWORD", password);
            DataAccess.update(uq);
            return true;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}