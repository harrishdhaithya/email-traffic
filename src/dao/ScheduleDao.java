package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.Row;
import com.adventnet.taskengine.CALENDAR;
import com.adventnet.taskengine.SCHEDULE;
import com.adventnet.taskengine.SCHEDULED_TASK;
import com.adventnet.taskengine.Scheduler;
import com.adventnet.taskengine.TASKENGINE_TASK;
import com.adventnet.taskengine.TASK_INPUT;
import com.adventnet.taskengine.util.CalendarRowConfig;
import model.Schedule;
import model.TaskSchedule;

public class ScheduleDao {
    private static ScheduleDao sdao = null;
    public static ScheduleDao getInstance(){
        if(sdao==null){
            sdao = new ScheduleDao();
        }
        return sdao;
    }
    private static Logger logger = Logger.getLogger(ScheduleDao.class.getName());
    public TaskSchedule getScheduleById(long scheduleid){
        Criteria c = new Criteria(new Column("Traffic_Schedule", "SCHEDULE_ID"), scheduleid, QueryConstants.EQUAL);
        TaskSchedule ts = null;
        try {
            DataObject dobj = DataAccess.get("Traffic_Schedule", c);
            Iterator itr = dobj.getRows("Traffic_Schedule");
            if(itr.hasNext()){
                Row r = (Row)itr.next();
                long id = r.getLong("ID");
                long tenant_id = r.getLong("TENANT_ID");
                long count = r.getLong("COUNT");
                ts = new TaskSchedule(id, tenant_id,scheduleid, count);
            }
        } catch (DataAccessException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return ts;
    }
    public boolean sheduleMailTask(String schedulename,int tenant_id,int count,LocalTime localTime)
    {
        try {
            Persistence pers = (Persistence) BeanUtil.lookup("Persistence");
            DataObject data = pers.constructDataObject();
            Row taskengineRow = new Row(TASKENGINE_TASK.TABLE);
            taskengineRow.set(TASKENGINE_TASK.TASK_NAME_IDX,schedulename);
            taskengineRow.set(TASKENGINE_TASK.CLASS_NAME_IDX,"task.MailTrafficTask");
            Row scheduleRow = new Row(SCHEDULE.TABLE);
            scheduleRow.set(SCHEDULE.SCHEDULE_NAME_IDX,schedulename);
            Row scheduledTaskRow = new Row(SCHEDULED_TASK.TABLE);
            scheduledTaskRow.set(SCHEDULED_TASK.SCHEDULE_ID_IDX,scheduleRow.get(SCHEDULE.SCHEDULE_ID_IDX));
            scheduledTaskRow.set(SCHEDULED_TASK.TASK_ID_IDX,taskengineRow.get(TASKENGINE_TASK.TASK_ID_IDX));
            CalendarRowConfig calRowConf = new CalendarRowConfig();
            calRowConf.setSkipFrequency(0);
            calRowConf.setScheduleType("DAILY");
            calRowConf.setFirstDayOfWeek(1);
            calRowConf.setExecutionTime(localTime.getHour(),localTime.getMinute(),localTime.getSecond());
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            calRowConf.setStartDate(cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH),cal.get(Calendar.YEAR));
            // System.out.println(calRowConf.getStartDate().toString());
            // calRowConf.setEndDate(endDate);
            // System.out.println(calRowConf.getEndDate().toString());
            Row calendarRow = calRowConf.toCalendarRow();
            calendarRow.set(CALENDAR.SCHEDULE_ID_IDX, scheduleRow.get(SCHEDULE.SCHEDULE_ID_IDX));
            Row trafficSchedule = new Row("Traffic_Schedule");
            trafficSchedule.set("TENANT_ID", tenant_id);
            trafficSchedule.set("SCHEDULE_ID", scheduleRow.get(SCHEDULE.SCHEDULE_ID_IDX));
            trafficSchedule.set("COUNT", count);
            data.addRow(trafficSchedule);
            data.addRow(taskengineRow);
            data.addRow(scheduleRow);
            data.addRow(calendarRow);
            data.addRow(scheduledTaskRow);
            pers.add(data);
            Scheduler s = (Scheduler) BeanUtil.lookup("Scheduler");
            DataObject taskInputDO = pers.constructDataObject();
            Row taskInputRow = new Row(TASK_INPUT.TABLE);
            taskInputDO.addRow(taskInputRow);
            s.scheduleTask(schedulename,schedulename,taskInputDO);
            return true;
        } catch (Exception e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    public List<Schedule> getSchedules(){
        List<Schedule> schedules = new LinkedList<>();
        SelectQuery sq = new SelectQueryImpl(new Table("Traffic_Schedule"));
        Join join1 = new Join("Traffic_Schedule", "Task_Input", new String[]{"SCHEDULE_ID"}, new String[]{"SCHEDULE_ID"}, Join.INNER_JOIN);
        Join join2 = new Join("Task_Input", "Schedule", new String[]{"SCHEDULE_ID"}, new String[]{"SCHEDULE_ID"}, Join.INNER_JOIN);
        sq.addJoin(join1);
        sq.addJoin(join2);
        sq.addSelectColumn(new Column("Traffic_Schedule","SCHEDULE_ID" ));
        sq.addSelectColumn(new Column("Traffic_Schedule","COUNT" ));
        sq.addSelectColumn(new Column("Traffic_Schedule","TENANT_ID" ));
        sq.addSelectColumn(new Column("Task_Input","SCHEDULE_TIME"));
        sq.addSelectColumn(new Column("Task_Input","ADMIN_STATUS"));
        sq.addSelectColumn(new Column("Schedule","SCHEDULE_NAME" ));
        RelationalAPI relApi = RelationalAPI.getInstance();
        Connection conn = null;
        DataSet ds = null;
        try {
            conn = relApi.getConnection();
            ds = relApi.executeQuery(sq,conn);
            while(ds.next()){
                long id = ds.getAsLong("SCHEDULE_ID");
                long count = ds.getAsLong("COUNT");
                long tenantid = ds.getAsLong("TENANT_ID");
                String scheduleTime = ds.getAsString("SCHEDULE_TIME").split(" ")[1];
                long adminStatus = ds.getAsLong("ADMIN_STATUS");
                String scheduleName = ds.getAsString("SCHEDULE_NAME");
                Schedule schedule = new Schedule(id, scheduleName, tenantid, scheduleTime, adminStatus,count);
                schedules.add(schedule);
            }
        } catch (QueryConstructionException e) {
            logger.warning(e.getMessage());
            e.printStackTrace();
        }catch(SQLException ex){
            logger.warning(ex.getMessage());
            ex.printStackTrace();
        }
        return schedules;
    }
    public long getTaskId(long scheduleid){
        Criteria c = new Criteria(new Column(SCHEDULED_TASK.TABLE, SCHEDULED_TASK.SCHEDULE_ID), scheduleid, QueryConstants.EQUAL);
        try {
            DataObject dobj = DataAccess.get(SCHEDULED_TASK.TABLE, c);
            Iterator itr = dobj.getRows(SCHEDULED_TASK.TABLE);
            if(itr.hasNext()){
                Row row = (Row)itr.next();
                long taskid = row.getLong(SCHEDULED_TASK.SCHEDULE_ID);
                return taskid;
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
