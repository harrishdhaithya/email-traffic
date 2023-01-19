package api;

import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.taskengine.Scheduler;

public class DisableSchedule extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        PrintWriter out = null;
        try {
            out = resp.getWriter();
            String id = req.getParameter("id");
            if(id==null){
                throw new NullPointerException("Not able to identify schedule");
            }
            Scheduler slr = (Scheduler)BeanUtil.lookup("Scheduler");
            slr.setTaskInputAdminStatus(Long.parseLong(id), Scheduler.DISABLE);
            resp.setStatus(200);
            resp.setContentType("application/json");
            JSONObject jobj = new JSONObject();
            jobj.put("message", "Task Disabled Successfully");
            out.println(jobj.toString());
            return;
        }catch(NullPointerException nuex){
            resp.setStatus(400);
            out.println(nuex.getMessage());
            nuex.printStackTrace();
            return;
        }catch (NumberFormatException e) {
            resp.setStatus(400);
            out.println("Id Should be numeric");
            e.printStackTrace();
            return;
        }catch(Exception ex){
            resp.setStatus(400);
            out.println(ex.getMessage());
            ex.printStackTrace();
            return;
        }
    }
}
