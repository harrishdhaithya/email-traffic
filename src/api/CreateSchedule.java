package api;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;

import dao.ScheduleDao;

public class CreateSchedule extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String taskname = req.getParameter("taskname");
        String schedulename = req.getParameter("schedulename");
        String startdate = req.getParameter("startdate");
        String timeperiod = req.getParameter("timeperiod");
        String timeunit = req.getParameter("timeunit");
        String tenantid = req.getParameter("tenantid");
        String count = req.getParameter("count");
        ScheduleDao sdao = ScheduleDao.getInstance();
        System.out.println(LocalDateTime.now().toString());
        boolean success = sdao.addTrafficSchedule("test_sh", "test_sh", "2023-01-10 22:00:00", 1, "minutes", 1, 100);
        resp.getWriter().println(success);
    }
}
