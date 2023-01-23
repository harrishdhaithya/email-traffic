package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import dao.TenantDao;
import model.Tenant;

public class UpdateTenant extends HttpServlet {
    private static final Logger logger = Logger.getLogger(UpdateTenant.class.getName());
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp){
       PrintWriter out=null;
       try {
            BufferedReader br = req.getReader();
            String line = "";
            StringBuilder sb = new StringBuilder();
            if((line=br.readLine())!=null){
                sb.append(line);
            }
            JSONObject jobj = new JSONObject(sb.toString());
            String id = jobj.getString("id");
            String name = jobj.getString("name");
            String app_clientid = jobj.getString("app_clientid");
            String admin_email = jobj.getString("admin_email");
            String admin_password = jobj.getString("admin_password");
            if(
                id==null ||
                app_clientid ==null 
            ){
                    throw new NullPointerException("All the fields are required...");
            }
            out = resp.getWriter();
            TenantDao tdao = TenantDao.getInstance();
            Tenant t = new Tenant(Long.parseLong(id), name, app_clientid, admin_email, admin_password);
            boolean success = tdao.updateTenant(t);
            if(!success){
                throw new Exception("Not able to Update Tenant");
            }
            resp.setStatus(200);
            resp.setContentType("application/json");
            JSONObject respObj = new JSONObject();
            respObj.put("message", "Tenant updated successfully...");
            out.println(respObj.toString());
        }catch(NullPointerException nex){
            logger.warning(nex.getMessage());
            nex.printStackTrace();
            resp.setStatus(400);
            out.println(nex.getMessage());
        }catch (IOException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
            resp.setStatus(400);
            out.println(e.getMessage());
        }catch(Exception ex){
            logger.warning(ex.getMessage());
            ex.printStackTrace();
            resp.setStatus(400);
            out.println(ex.getMessage());
        }
    }
}
