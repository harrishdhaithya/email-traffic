package api;

import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.taskengine.Scheduler;

public class EnableSchedule extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        PrintWriter out = null;
        try{
            out = resp.getWriter();
            String id = req.getParameter("id");
            if(id==null){
                throw new NullPointerException("");
            }
            Scheduler shr = (Scheduler)BeanUtil.lookup("Scheduler");
            shr.setTaskInputAdminStatus(Long.parseLong(id), Scheduler.ENABLE);
            resp.setStatus(200);
            resp.setContentType("application/json");
            JSONObject jobj = new JSONObject();
            jobj.put("message", "Schedule Enabled Successfully...");
            out.println(jobj.toString());
        }catch(NullPointerException nuex){
            nuex.printStackTrace();
            resp.setStatus(400);
            out.println(nuex.getMessage());
        }catch(NumberFormatException nex){
            resp.setStatus(400);
            out.println("Invalid task...");
        }catch(Exception ex){
            resp.setStatus(400);
            out.println("Not able to Enable Schedule...");
        }
    }
}
