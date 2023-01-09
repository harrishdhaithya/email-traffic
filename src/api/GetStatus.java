package api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import controller.MailTrafficGenerator;

public class GetStatus extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        MailTrafficGenerator gen = (MailTrafficGenerator)session.getAttribute("generator");
        JSONObject jobj = new JSONObject();
        if(gen==null){
            resp.setStatus(400);
            resp.setContentType("application/json");
            jobj.put("error", "Something went wrong...");
            resp.getWriter().println(jobj.toString());
            return;
        }
        if(gen.isCompleted()){
            session.invalidate();
        }
        resp.setStatus(200);
        resp.setContentType("application/json");
        resp.getWriter().println(gen.getStatus());
        return;
    }
}
