package api;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.time.LocalTime;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.taskengine.Scheduler;
import dao.ScheduleDao;

public class ScheduleServ extends HttpServlet {
    private static Logger logger = Logger.getLogger(ScheduleServ.class.getName());
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String schedulename = req.getParameter("schedulename");
        String time = req.getParameter("time");
        String count = req.getParameter("count");
        String tenant_id = req.getParameter("tenantid");
        PrintWriter out = null;
        ScheduleDao sdao = ScheduleDao.getInstance();
        try {
            out = resp.getWriter();
            if(
                schedulename == null ||
                time==null ||
                count == null ||
                tenant_id == null
            ){
                throw new Exception("Fill all the required fields...");
            }
            LocalTime lt = LocalTime.parse(time);
            boolean success = sdao.sheduleMailTask(schedulename, Integer.parseInt(tenant_id), Integer.parseInt(count),lt);
            if(!success){
                throw new Exception("Not able to schedule task...");
            }
            JSONObject jobj = new JSONObject();
            jobj.put("message", "Task sheduled successfully...");
            resp.setStatus(200);
            resp.setContentType("application/json");
            out.println(jobj.toString());
        }catch(NumberFormatException nex){
            logger.warning(nex.getMessage());
            nex.printStackTrace();
            resp.setStatus(400);
            out.println("Invalid Fields");
        }catch (ParseException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
            resp.setStatus(400);
            out.println("Incorrect Data Format");
        }catch(Exception ex){
            logger.warning(ex.getMessage());
            ex.printStackTrace();
            resp.setStatus(400);
            out.println(ex.getMessage());
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        PrintWriter out = null;
        try{
            out = resp.getWriter();
            String id = req.getParameter("id");
            if(id==null){
                throw new NullPointerException("Invalid Schedule ID");
            }
            boolean status = Boolean.valueOf(req.getParameter("status"));
            Scheduler shr = (Scheduler)BeanUtil.lookup("Scheduler");
            shr.setTaskInputAdminStatus(Long.parseLong(id), status?Scheduler.ENABLE:Scheduler.DISABLE);
            resp.setStatus(200);
            resp.setContentType("application/json");
            JSONObject jobj = new JSONObject();
            jobj.put("message", "Schedule "+(status?"Enabled":"Disabled")+" Successfully...");
            out.println(jobj.toString());
        }catch(NullPointerException nuex){
            logger.info(nuex.getMessage());
            nuex.printStackTrace();
            resp.setStatus(400);
            out.println(nuex.getMessage());
        }catch(NumberFormatException nex){
            logger.warning(nex.getMessage());
            nex.printStackTrace();
            resp.setStatus(400);
            out.println("Invalid task...");
        }catch(Exception ex){
            logger.warning(ex.getMessage());
            ex.printStackTrace();
            resp.setStatus(400);
            out.println("Not able to Enable Schedule...");
        }
    }
}
