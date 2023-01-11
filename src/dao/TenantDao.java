package dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import model.Tenant;

public class TenantDao {
    private static TenantDao tdao = null;
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
        try {
            dobj.addRow(row);
            DataAccess.add(dobj);
            return true;
        } catch (DataAccessException e) {
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
                t = new Tenant(id, tenant_name);
            } 
        } catch (DataAccessException e) {
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
                Tenant t = new Tenant(id, tenant_name);
                tenants.add(t);
            }
        }catch(DataAccessException dex){
            dex.printStackTrace();
        }
        return tenants;
    }
}
