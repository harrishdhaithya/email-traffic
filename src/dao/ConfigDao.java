package dao;

import java.util.Iterator;
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
import model.Config;

public class ConfigDao {
    private static Logger logger = Logger.getLogger(ConfigDao.class.getName());
    private static ConfigDao cdao = null;
    public static ConfigDao getInstance(){
        if(cdao==null){
            cdao = new ConfigDao();
        }
        return cdao;
    }
    public boolean addConfig(Config c){
        DataObject dobj = new WritableDataObject();
        Row row = new Row("App_Configuration");
        row.set("PROPERTY_NAME", c.getPropName());
        row.set("PROPERTY_VALUE", c.getPropValue());
        try {
            dobj.addRow(row);
            DataAccess.update(dobj);
            return true;
        } catch (DataAccessException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateConfig(Config conf){
        Criteria c = new Criteria(new Column("App_Configuration", "PROPERTY_NAME"), conf.getPropName(), QueryConstants.EQUAL);
        UpdateQuery uq = new UpdateQueryImpl("App_Configuration");
        uq.setCriteria(c);
        try{
            uq.setUpdateColumn("PROPERTY_VALUE", conf.getPropValue());
            DataAccess.update(uq);
            return true;
        } catch (DataAccessException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    public Config getConfig(String prop_name){
        Config conf = null;
        Criteria c = new Criteria(new Column("App_Configuration", "PROPERTY_NAME"), prop_name, QueryConstants.EQUAL);
        try {
            DataObject dobj = DataAccess.get("App_Configuration", c);
            Iterator<?> itr = dobj.getRows("App_Configuration");
            if(itr.hasNext()){
                Row row = (Row)itr.next();
                long id = row.getLong("ID");
                String prop_value = row.getString("PROPERTY_VALUE");
                conf = new Config(id,prop_name, prop_value);
            }
        } catch (DataAccessException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return conf;
    }
}
