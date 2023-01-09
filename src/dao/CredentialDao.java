package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import model.Credential;

public class CredentialDao {
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
            e.printStackTrace();
        }
        return false;
    }
    public List<Credential> getCredentials(long tenantId){
        List<Credential> creds = new LinkedList<>();
        try {
            Criteria c = new Criteria(new Column("Credentials","TENANT_ID"), tenantId, QueryConstants.EQUAL);
            DataObject dobj = DataAccess.get("Credentials", c);
            Iterator<?> itr = dobj.getRows("Credentials");
            while(itr.hasNext()){
                Row row = (Row)itr.next();
                long id = row.getInt("ID");
                String email = row.getString("EMAIL");
                String password = row.getString("PASSWORD");
                Credential cred = new Credential(id, email, password, id);
                creds.add(cred);
            }
            return creds;
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONObject getSenderAndRecCount(long tenant_id){
        Criteria c1 = new Criteria(new Column("Credentials","PASSWORD"),null , QueryConstants.NOT_EQUAL);
        Criteria c2 = new Criteria(new Column("Credentials", "PASSWORD"),null,QueryConstants.EQUAL);
        Criteria tenant = new Criteria(new Column("Credentials", "TENANT_ID"), tenant_id,QueryConstants.EQUAL);
        JSONObject jobj = null;
        try {
            jobj = new JSONObject();
            DataObject dobj1 = DataAccess.get("Credentials",c1.and(tenant));
            DataObject dobj2 = DataAccess.get("Credentials", c2.and(tenant));
            int size1 = dobj1.size("Credentials");
            int size2 = dobj2.size("Credentials");
            jobj.put("sender", (size1==-1)?0:size1);
            jobj.put("reciever", (size2==-1)?0:size2);
            return jobj;
        } catch (DataAccessException e) {
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
            ds=relApi.executeQuery(conn, "select * from credentials c where tenant_id="+tenantid+" and password IS NOT NULL order by random() limit 1");
            if(ds.next()){
                long id = ds.getAsLong("ID");
                String email = ds.getAsString("EMAIL");
                String password = ds.getAsString("PASSWORD");
                long tenantId = ds.getAsLong("TENANT_ID");
                c = new Credential(id,email, password, tenantId);
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return c;
    }
    public String getRandomReciever(long tenant_id){
        RelationalAPI relApi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = relApi.getConnection();
            ds=relApi.executeQuery(conn, "select EMAIL from credentials c where tenant_id="+tenant_id+" and password IS NULL order by random() limit 1");
            if(ds.next()){
                String email = ds.getAsString("EMAIL");
                conn.close();
                return email;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}