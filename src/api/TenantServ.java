package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import dao.CredentialDao;
import dao.TenantDao;
import model.Tenant;

public class TenantServ extends HttpServlet {
    Logger logger = Logger.getLogger(this.getClass().getName());
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = null;
        try{
            out = resp.getWriter();
            String tenant = req.getParameter("tenantid");
            if(tenant==null){
                throw new Exception("No tenant id found");
            }
            CredentialDao cdao = CredentialDao.getInstance();
            resp.setStatus(200);
            resp.setContentType("application/json");
            out.println(cdao.getSenderAndRecCount(Long.parseLong(tenant)));
            return;
        }catch(Exception ex){
            logger.warning(ex.toString());
            resp.setStatus(400);
            out.println(ex.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = null;
        JSONObject jobj = new JSONObject();
        try{
            out = resp.getWriter();
            String tenant = req.getParameter("tenant");
            String app_clientid = req.getParameter("app_clientid");
            String adminEmail = req.getParameter("admin_email");
            String adminPassword = req.getParameter("admin_password");
            if(tenant==null){
                throw new Exception("Enter Tenant Name");
            }
            if(app_clientid==null){
                throw new Exception("Enter App ClientId");
            }
            Tenant t = new Tenant(tenant, app_clientid, adminEmail, adminPassword);
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
            ex.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out=null;
       try {
            String id = req.getParameter("id");
            String name = req.getParameter("name");
            String app_clientid = req.getParameter("app_clientid");
            String admin_email = req.getParameter("admin_email");
            String admin_password = req.getParameter("admin_password");
            BufferedReader br = req.getReader();
            String line = "";
            while((line=br.readLine())!=null){
                logger.info(line);
            }
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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        PrintWriter out = null;
        try{
            out = resp.getWriter();
            TenantDao tdao = TenantDao.getInstance();
            boolean success = tdao.deleteTenant(Long.parseLong(id));
            if(!success){
                throw new Exception("Not able to delete Tenant...");
            }
            resp.setStatus(200);
            resp.setContentType("application/json");
            JSONObject jobj = new JSONObject();
            jobj.put("message", "Successfully Deleted Tenant...");
            out.println(jobj.toString());
        }catch(NullPointerException nuex){
            resp.setStatus(400);
            out.println(nuex.getMessage());
            nuex.printStackTrace();
            return;
        }catch(NumberFormatException nex){
            resp.setStatus(400);
            out.println("Id should be a number");
            nex.printStackTrace();
            return;
        }catch(Exception ex){
            resp.setStatus(400);
            out.println(ex.getMessage());
            ex.printStackTrace();
            return;
        }
    }
    
}
