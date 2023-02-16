package api;

import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import dao.MailTraceDao;

public class TraceHistory extends HttpServlet {
    private static Logger logger = Logger.getLogger(Logger.class.getName());
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        PrintWriter out = null;
        try{
            out = resp.getWriter();
            String tenant = req.getParameter("tenantid");
            MailTraceDao mDao = MailTraceDao.getInstance();
            JSONArray jarr = null;
            if(tenant==null||tenant.trim().equals("")){
                jarr = mDao.getAllTraces();
            }else{
                long tenantid = Long.parseLong(tenant);
                jarr = mDao.getTraceStatusByTenant(tenantid);
            }
            JSONObject result = new JSONObject();
            result.put("data", jarr);
            resp.setStatus(200);
            resp.setContentType("application/json");
            out.println(result.toString());
        }catch(Exception ex){
            logger.warning("Not able to get Trace History...");
            ex.printStackTrace();
            resp.setStatus(400);
            out.println("Not able to get Trace History...");
        }
        
    }

}
