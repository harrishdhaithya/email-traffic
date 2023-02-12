package api;

import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import messagetrace.MessageTrace;

public class MailTraceStatus extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        HttpSession session = req.getSession(false);
        PrintWriter out = null;
        try{
            out = resp.getWriter();
            if(session==null){
                throw new Exception("No MailTraces Session Found");
            }
            MessageTrace mt = (MessageTrace)session.getAttribute("mailtrace");
            resp.setContentType("application/json");
            resp.setStatus(200);
            out.println(mt.getStatus().toString());
        }catch(Exception ex){
            ex.printStackTrace();
            resp.setStatus(400);
            out.println(ex.getMessage());
        }
    }
}
