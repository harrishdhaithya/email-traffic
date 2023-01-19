package api;

import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import dao.TenantDao;

public class DeleteTenant extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
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
