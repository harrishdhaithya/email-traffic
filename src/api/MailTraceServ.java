package api;


import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import dao.MailTraceDao;
import messagetrace.MessageTrace;
import model.MailTrace;

public class MailTraceServ extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp){
        PrintWriter out = null;
        try{
            out = resp.getWriter();
            String tenant = req.getParameter("tenant");
            long tenantid = Long.parseLong(tenant);
            MessageTrace mt = new MessageTrace(tenantid);
            mt.updateTraceAsync();
            HttpSession session = req.getSession();
            session.setAttribute("mailtrace", mt);
            resp.setStatus(200);
            resp.setContentType("application/json");
            JSONObject jobj = new JSONObject();
            jobj.put("message", "MailTrace Update Session Started...");
            out.println(jobj.toString());
        }catch(Exception ex){
            ex.printStackTrace();
            resp.setStatus(400);
            out.println("Not able to Schedule MessageTrace...");
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        String lowerbound = req.getParameter("lowerbound");
        String tenant = req.getParameter("tenant");
        String count = req.getParameter("count");
        PrintWriter out = null;
        try{
            out = resp.getWriter();
            int lower = 1;
            int countNum = 100;
            if(lowerbound!=null){
                lower = Integer.parseInt(lowerbound);
            }
            if(count!=null){
                countNum = Integer.parseInt(count);
            }
            int tenantid = Integer.parseInt(tenant);
            MailTraceDao mtdao = MailTraceDao.getInstance();
            List<MailTrace> mailtraces = mtdao.getMailTraces(tenantid, lower, countNum);
            long totalcount = mtdao.getMailTraceCount(tenantid);
            JSONObject resObj = new JSONObject();
            resObj.put("totalcount", totalcount);
            resObj.put("rangestart", lower);
            resObj.put("rangeend", lower+mailtraces.size()-1);
            JSONArray data = new JSONArray();
            for(MailTrace mt:mailtraces){
                JSONObject jobj = new JSONObject();
                jobj.put("sender", mt.getSender());
                jobj.put("receiver", mt.getReceiver());
                jobj.put("subject", mt.getSubject());
                jobj.put("timestamp", mt.getTimestamp());
                jobj.put("status", mt.getStatus());
                data.put(jobj);
            }
            resObj.put("data", data.toString());
            resp.setStatus(200);
            resp.setContentType("application/json");
            out.println(resObj.toString());
        }catch(Exception ex){
            ex.printStackTrace();
            resp.setStatus(400);
            out.println("Not able to get MailTraces...");
        }
    }
}
