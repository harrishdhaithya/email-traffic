package api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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

@MultipartConfig
public class TrafficServ extends HttpServlet {
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
                String tenant_id = req.getParameter("tenant");
                if(p==null){
                    resp.setStatus(400);
                    jobj.put("error", "Please Upload the file");
                    out.println(jobj.toString());
                    return;
                }
                if(!p.getContentType().equals("text/csv")){
                    throw new Exception("Please upload a csv file...");
                }
                // String filename ="../../../../files/" + p.getSubmittedFileName();
                // p.write(filename);
                InputStream is = p.getInputStream();
                StringBuilder sb = new StringBuilder();
                String line = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while ((line=br.readLine())!=null) {
                    sb.append(line+"\n");
                }
                gen = new MailTrafficGenerator(count, sb.toString(),Long.parseLong(tenant_id));
                
            }else if(datasource.equals("sequence")){
                String prefix = req.getParameter("prefix");
                String suffix = req.getParameter("suffix");
                String tenantid = req.getParameter("tenant");
                String password = req.getParameter("password");
                String seqStartVal = req.getParameter("seqstart");
                String seqEndVal = req.getParameter("seqend");
                logger.info(tenantid);
                if(
                    prefix==null ||
                    tenantid==null ||
                    password == null ||
                    seqStartVal == null ||
                    seqEndVal == null
                ){  
                    throw new Exception("Please fill the required fields...");
                }
                int seqStart = Integer.parseInt(seqStartVal);
                int seqEnd = Integer.parseInt(seqEndVal);
                gen = new MailTrafficGenerator(count, seqStart, seqEnd, prefix, suffix, password,Long.parseLong(tenantid));
            }else{
                String tenant_id = req.getParameter("tenant");
                if(tenant_id==null||tenant_id==""){
                    throw new Exception("Please select Tenant...");
                }
                CredentialDao cdao = CredentialDao.getInstance();
                JSONObject teninfo = cdao.getSenderAndRecCount(Long.parseLong(tenant_id));
                int senders = teninfo.getInt("sender");
                // int recievers = teninfo.getInt("receiver");
                if(senders==0){
                    throw new Exception("No Senders found");
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
            servex.printStackTrace();
            logger.warning(servex.getStackTrace().toString());
            resp.setStatus(400);
            out.println(servex.getMessage());
            return;
        }
    }
}