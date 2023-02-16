package api;

import java.io.PrintWriter;
import java.time.ZoneId;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import dao.TenantDao;
import dao.TrafficHistoryDao;
import model.TrafficHistory;

public class TrafficHistoryServ extends HttpServlet {
    private static final Logger logger = Logger.getLogger(TrafficHistoryServ.class.getName());
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        PrintWriter out = null;
        try{
            out = resp.getWriter();
            String tenant = req.getParameter("tenantid");
            List<TrafficHistory> hist = null;
            TrafficHistoryDao thdao = TrafficHistoryDao.getInstance();
            TenantDao tdao = TenantDao.getInstance();
            if(tenant==null||tenant.trim().equals("")){
                hist = thdao.getAllTrafficHistory();
            }else{
                long tenantid = Long.parseLong(tenant);
                hist = thdao.getTrafficHistories(tenantid);
            }
            JSONArray jarr = new JSONArray();
            for(TrafficHistory th:hist){
                JSONObject jobj = new JSONObject();
                jobj.put("id", th.getId());
                jobj.put("tenant", tdao.getTenant(th.getTenantid()).getName());
                jobj.put("successCount", th.getSuccessCount());
                jobj.put("failureCount", th.getFailureCount());
                jobj.put("totalCount", th.getTotalCount());
                jobj.put("startTime", th.getStarttime().toString());
                jobj.put("endTime", th.getEndtime().toString());
                jarr.put(jobj);
            }
            JSONObject result = new JSONObject();
            result.put("data", jarr);
            resp.setStatus(200);
            resp.setContentType("application/json");
            out.println(result.toString());
            return;
        }catch(Exception ex){
            logger.warning(ex.getMessage());
            ex.printStackTrace();
            resp.setStatus(400);
            out.println("Not able to load History");
        }
    }
}
