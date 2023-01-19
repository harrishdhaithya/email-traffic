package api;

import java.io.PrintWriter;
import java.text.ParseException;
import java.time.LocalTime;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import dao.ScheduleDao;

public class CreateSchedule extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
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
            nex.printStackTrace();
            resp.setStatus(400);
            out.println("Invalid Fields");
        }catch (ParseException e) {
            e.printStackTrace();
            resp.setStatus(400);
            out.println("Incorrect Data Format");
        }catch(Exception ex){
            ex.printStackTrace();
            resp.setStatus(400);
            out.println(ex.getMessage());
        }
    }
}
