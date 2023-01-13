package dao;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.logging.Logger;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstants;
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
import model.TaskSchedule;

public class ScheduleDao {
    private static ScheduleDao sdao = null;
    public static ScheduleDao getInstance(){
        if(sdao==null){
            sdao = new ScheduleDao();
        }
        return sdao;
    }
    private Logger logger = Logger.getLogger(this.getClass().getName());
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
            e.printStackTrace();
        }
        return ts;
    }
    public boolean sheduleMailTask(String taskname, String schedulename,Timestamp startDate, Timestamp endDate,int tenant_id,int count)
    {
        try {
            Persistence pers = (Persistence) BeanUtil.lookup("Persistence");
            DataObject data = pers.constructDataObject();
            Row taskengineRow = new Row(TASKENGINE_TASK.TABLE);
            taskengineRow.set(TASKENGINE_TASK.TASK_NAME_IDX,taskname);
            taskengineRow.set(TASKENGINE_TASK.CLASS_NAME_IDX,"task.MailTrafficTask");
            Row scheduleRow = new Row(SCHEDULE.TABLE);
            scheduleRow.set(SCHEDULE.SCHEDULE_NAME_IDX,schedulename);
            Row scheduledTaskRow = new Row(SCHEDULED_TASK.TABLE);
            scheduledTaskRow.set(SCHEDULED_TASK.SCHEDULE_ID_IDX,scheduleRow.get(SCHEDULE.SCHEDULE_ID_IDX));
            scheduledTaskRow.set(SCHEDULED_TASK.TASK_ID_IDX,taskengineRow.get(TASKENGINE_TASK.TASK_ID_IDX));
            CalendarRowConfig calRowConf = new CalendarRowConfig();
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            calRowConf.setSkipFrequency(0);
            calRowConf.setScheduleType("DAILY");
            calRowConf.setFirstDayOfWeek(1);
            calRowConf.setExecutionTime(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),cal.get(Calendar.SECOND));
            calRowConf.setStartDate(cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH),cal.get(Calendar.YEAR));
            System.out.println(calRowConf.getStartDate().toString());
            calRowConf.setEndDate(endDate);
            System.out.println(calRowConf.getEndDate().toString());
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
            s.scheduleTask(schedulename,taskname,taskInputDO);
            return true;
        } catch (Exception e) {
            logger.info(e.toString());
            e.printStackTrace();
        }
        return false;
    }
}
