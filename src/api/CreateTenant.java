package api;

import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import dao.TenantDao;
import model.Tenant;

public class CreateTenant extends HttpServlet {
    Logger logger = Logger.getLogger(this.getClass().getName());
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        PrintWriter out = null;
        JSONObject jobj = new JSONObject();
        try{
            out = resp.getWriter();
            String tenant = req.getParameter("tenant");
            if(tenant==null){
                throw new Exception("Enter Tenant Name");
            }
            Tenant t = new Tenant(tenant);
            TenantDao tdao = TenantDao.getInstance();
            boolean success = tdao.addTenant(t);
            if(!success){
                throw new Exception("Not able to add tenant");
            }
            resp.setStatus(200);
            resp.setContentType("application/json");
            jobj.put("message", "Tenant Created Successfully");
            out.println(jobj.toString());
        }catch(Exception ex){
            logger.warning(ex.toString());
            resp.setStatus(400);
            // resp.setContentType("application/json");
            // jobj.put("error", ex.getMessage());
            out.println(ex.getMessage());
        }
    }
}
