package api;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import dao.ScheduleDao;

public class CreateSchedule extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        String taskname = req.getParameter("taskname");
        String schedulename = req.getParameter("schedulename");
        String startdate = req.getParameter("startdate");
        String enddate = req.getParameter("enddate");
        String count = req.getParameter("count");
        String tenant_id = req.getParameter("tenantid");
        String frequency = req.getParameter("frequency");
        PrintWriter out = null;
        ScheduleDao sdao = ScheduleDao.getInstance();
        try {
            out = resp.getWriter();
            if(
                taskname == null ||
                schedulename == null ||
                enddate == null ||
                startdate == null ||
                count == null ||
                tenant_id == null
            ){
                throw new Exception("Fill all the required fields...");
            }
            LocalDateTime startDateTS = LocalDateTime.parse(startdate);
            LocalDateTime endDateTS = LocalDateTime.parse(enddate);
            Timestamp start = Timestamp.valueOf(startDateTS);
            Timestamp end = Timestamp.valueOf(endDateTS);
            boolean success = sdao.sheduleMailTask(taskname, schedulename, start, end, Integer.parseInt(tenant_id), Integer.parseInt(count));
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
