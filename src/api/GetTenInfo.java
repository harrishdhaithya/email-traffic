package api;

import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.CredentialDao;
import singleton.Singleton;

public class GetTenInfo extends HttpServlet {
    Logger logger = Logger.getLogger(this.getClass().getName());
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        PrintWriter out = null;
        try{
            out = resp.getWriter();
            String tenant = req.getParameter("tenantid");
            if(tenant==null){
                throw new Exception("No tenant id found");
            }
            CredentialDao cdao = Singleton.getCredentialDao();
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
}