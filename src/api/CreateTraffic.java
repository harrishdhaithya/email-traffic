package api;

import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.json.JSONObject;
import controller.MailTrafficGenerator;
import dao.CredentialDao;
import singleton.Singleton;


@MultipartConfig
public class CreateTraffic extends HttpServlet {
    Logger logger = Logger.getLogger(this.getClass().getName());
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        PrintWriter out = null;
        JSONObject jobj = null;
        try{
            out = resp.getWriter();
            jobj = new JSONObject();
            String countStr = req.getParameter("count");
            String datasource = req.getParameter("datasource");
            if(countStr== null){
                throw new Exception("Value of count is required");
            }
            int count = Integer.parseInt(countStr);
            if(count<=0){
                throw new Exception("The value of count must be positive");
            }
            MailTrafficGenerator gen = null;
            if(datasource.equals("csv")){
                Part p = req.getPart("file");
                if(p==null){
                    resp.setStatus(400);
                    jobj.put("error", "Please Upload the file");
                    out.println(jobj.toString());
                    return;
                }
                String filename ="E:\\AdventNet\\MickeyLite\\files\\" + p.getSubmittedFileName();
                p.write(filename);
                if(!p.getContentType().equals("text/csv")){
                    throw new Exception("Please upload a csv file...");
                }
                gen = new MailTrafficGenerator(count, filename);
                
            }else if(datasource.equals("sequence")){
                String prefix = req.getParameter("prefix");
                String suffix = req.getParameter("suffix");
                String tenant = req.getParameter("tenant");
                String password = req.getParameter("password");
                String seqStartVal = req.getParameter("seqstart");
                String seqEndVal = req.getParameter("seqend");
                if(
                    prefix==null ||
                    tenant==null ||
                    password == null ||
                    seqStartVal == null ||
                    seqEndVal == null
                ){
                    throw new Exception("Please fill the required fields...");
                }
                int seqStart = Integer.parseInt(seqStartVal);
                int seqEnd = Integer.parseInt(seqEndVal);
                gen = new MailTrafficGenerator(count, tenant, seqStart, seqEnd, prefix, suffix, password);
            }else{
                String tenant_id = req.getParameter("tenant");
                if(tenant_id==null||tenant_id==""){
                    throw new Exception("Please select Tenant...");
                }
                CredentialDao cdao = Singleton.getCredentialDao();
                JSONObject teninfo = cdao.getSenderAndRecCount(Long.parseLong(tenant_id));
                int senders = teninfo.getInt("sender");
                int recievers = teninfo.getInt("reciever");
                if(senders==0){
                    throw new Exception("No Senders found");
                }
                if(recievers==0){
                    throw new Exception("No Recievers found");
                }
                gen = new MailTrafficGenerator(Long.parseLong(tenant_id),count);
            }
            gen.generateTraffic();
            HttpSession session = req.getSession(true);
            session.setAttribute("generator", gen);
            jobj.put("message", "Generation Started...");
            resp.setContentType("application/json");
            resp.setStatus(200);
            out.println(jobj.toString());
            return;
        }catch(Exception servex){
            // servex.printStackTrace();
            logger.warning(servex.getStackTrace().toString());
            resp.setStatus(400);
            jobj.put("error", servex.getMessage());
            out.println(jobj.toString());
            return;
        }
    }
}
