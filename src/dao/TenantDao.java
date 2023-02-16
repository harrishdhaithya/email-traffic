package dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import model.Tenant;

public class TenantDao {
    private static TenantDao tdao = null;
    private static Logger logger = Logger.getLogger(TenantDao.class.getName());
    public static TenantDao getInstance(){
        if(tdao==null){
            tdao = new TenantDao();
        }
        return tdao;
    }
    public boolean addTenant(Tenant t){
        DataObject dobj = new WritableDataObject();
        Row row = new Row("Tenant");
        row.set("TENANT_NAME", t.getName().toLowerCase());
        row.set("APP_CLIENT_ID",t.getAppClientId());
        row.set("ADMIN_USERNAME", t.getAdminEmail());
        row.set("ADMIN_PASSWORD",t.getAdminPassword());
        try {
            dobj.addRow(row);
            DataAccess.add(dobj);
            return true;
        } catch (DataAccessException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateTenant(Tenant t){
        Criteria c = new Criteria(new Column("Tenant", "ID"), t.getId(), QueryConstants.EQUAL);
        UpdateQuery uq = new UpdateQueryImpl("Tenant");
        uq.setCriteria(c);
        uq.setUpdateColumn("TENANT_NAME", t.getName());
        uq.setUpdateColumn("APP_CLIENT_ID", t.getAppClientId());
        uq.setUpdateColumn("ADMIN_USERNAME", t.getAdminEmail());
        uq.setUpdateColumn("ADMIN_PASSWORD", t.getAdminPassword());
        try {
            DataAccess.update(uq);
            return true;
        } catch (DataAccessException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    public Tenant getTenant(long id){
        Tenant t = null;
        Criteria c = new Criteria(new Column("Tenant", "ID"), id, QueryConstants.EQUAL);
        try {
            DataObject dobj = DataAccess.get("Tenant", c);
            Iterator<?> itr = dobj.getRows("Tenant");
            if(itr.hasNext()){
                Row row = (Row)itr.next();
                String tenant_name = row.getString("TENANT_NAME");
                String app_clientid = row.getString("APP_CLIENT_ID");
                String admin_email = row.getString("ADMIN_USERNAME");
                String admin_password = row.getString("ADMIN_PASSWORD");
                t = new Tenant(id, tenant_name,app_clientid,admin_email,admin_password);
            } 
        } catch (DataAccessException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return t;
    }
    public List<Tenant> getAllTenants(){
        List<Tenant> tenants = new LinkedList<>();
        try{
            DataObject dobj = DataAccess.get("Tenant", (Criteria)null);
            Iterator<?> itr = dobj.getRows("Tenant");
            while(itr.hasNext()){
                Row row = (Row)itr.next();
                long id = row.getLong("ID");
                String tenant_name = row.getString("TENANT_NAME");
                String app_clientid = row.getString("APP_CLIENT_ID");
                // String azure_tenantid = row.getString("AZURE_TENANT_ID");
                String admin_email = row.getString("ADMIN_USERNAME");
                String admin_password = row.getString("ADMIN_PASSWORD");
                Tenant t = new Tenant(id, tenant_name,app_clientid,admin_email,admin_password);
                tenants.add(t);
            }
        }catch(DataAccessException dex){
            logger.warning(dex.getMessage());
            dex.printStackTrace();
        }
        return tenants;
    }
    public boolean deleteTenant(long id){
        Criteria c = new Criteria(new Column("Tenant", "ID"), id, QueryConstants.EQUAL);
        try {
            DataAccess.delete("Tenant", c);
            return true;
        } catch (DataAccessException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
