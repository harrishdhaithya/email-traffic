package dao;

import java.util.Iterator;
import java.util.logging.Logger;

import org.json.JSONObject;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.taskengine.PERIODIC;
import com.adventnet.taskengine.SCHEDULE;
import com.adventnet.taskengine.SCHEDULEDTASK_RETRY;
import com.adventnet.taskengine.SCHEDULED_TASK;
import com.adventnet.taskengine.Scheduler;
import com.adventnet.taskengine.TASKENGINE_TASK;
import com.adventnet.taskengine.TASK_INPUT;

import model.Config;
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
    public TaskSchedule getScheduleById(long id){
        Criteria c = new Criteria(new Column("Traffic_Schedule", "TENANT_ID"), id, QueryConstants.EQUAL);
        TaskSchedule ts = null;
        try {
            DataObject dobj = DataAccess.get("Traffic_Schedule", c);
            Iterator itr = dobj.getRows("Traffic_Schedule");
            if(itr.hasNext()){
                Row r = (Row)itr.next();
                long tenant_id = r.getLong("TENANT_ID");
                long count = r.getLong("COUNT");
                ts = new TaskSchedule(id, tenant_id, count);
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
        }
        return ts;
    }
    public boolean addTrafficSchedule(String taskname, String schedulename, String startdate, long timeperiod, String unit,long tenant_id,long count){
        Persistence pers;
        try {
            pers = (Persistence) BeanUtil.lookup("Persistence");
            ConfigDao cdao = ConfigDao.getInstance();
            Config classConf = cdao.getConfig("CLASS_NAME");
            Config retryCountConf = cdao.getConfig("RETRY_COUNT");
            Config retryFactorConf = cdao.getConfig("RETRY_FACTOR");
            Config retryTimePeriod = cdao.getConfig("RETRY_TIME_PERIOD");
            Config retryTimeUnit = cdao.getConfig("RETRY_UNIT_OF_TIME");
            Config retryHandler = cdao.getConfig("RETRY_HANDLER");
            DataObject dobj = pers.constructDataObject();
            
            Row taskengineRow = new Row(TASKENGINE_TASK.TABLE);
            taskengineRow.set(TASKENGINE_TASK.TASK_NAME_IDX,taskname);
            taskengineRow.set(TASKENGINE_TASK.CLASS_NAME_IDX,classConf.getPropValue());
            dobj.addRow(taskengineRow);
            Row scheduleRow = new Row(SCHEDULE.TABLE);
            scheduleRow.set(SCHEDULE.SCHEDULE_NAME_IDX,schedulename);
            dobj.addRow(scheduleRow);            
            Row scheduledTaskRow = new Row(SCHEDULED_TASK.TABLE);
            scheduledTaskRow.set(SCHEDULED_TASK.SCHEDULE_ID_IDX,scheduleRow.get(SCHEDULE.SCHEDULE_ID_IDX));
            scheduledTaskRow.set(SCHEDULED_TASK.TASK_ID_IDX,taskengineRow.get(TASKENGINE_TASK.TASK_ID_IDX));
            scheduledTaskRow.set(SCHEDULED_TASK.RETRY_HANDLER,"com.adventnet.taskengine.internal.DefaultScheduleRetryHandler");
            dobj.addRow(scheduledTaskRow);
            Row scheduledTaskRetryRow = new Row(SCHEDULEDTASK_RETRY.TABLE);
            scheduledTaskRetryRow.set(SCHEDULEDTASK_RETRY.SCHEDULE_ID_IDX ,scheduleRow.get(SCHEDULE.SCHEDULE_ID_IDX));
            scheduledTaskRetryRow.set(SCHEDULEDTASK_RETRY.TASK_ID_IDX,taskengineRow.get(TASKENGINE_TASK.TASK_ID_IDX));
            scheduledTaskRetryRow.set(SCHEDULEDTASK_RETRY.RETRY_COUNT,5);
            scheduledTaskRetryRow.set(SCHEDULEDTASK_RETRY.RETRY_FACTOR,5);
            scheduledTaskRetryRow.set(SCHEDULEDTASK_RETRY.RETRY_TIME_PERIOD,10);
            scheduledTaskRetryRow.set(SCHEDULEDTASK_RETRY.RETRY_UNIT_OF_TIME,"minutes");
            dobj.addRow(scheduledTaskRetryRow);
            Row periodicRow = new Row(PERIODIC.TABLE);
            periodicRow.set(PERIODIC.SCHEDULE_ID_IDX, scheduleRow.get(SCHEDULE.SCHEDULE_ID_IDX));
            periodicRow.set(PERIODIC.START_DATE_IDX,startdate);
            periodicRow.set(PERIODIC.TIME_PERIOD_IDX,timeperiod);
            periodicRow.set(PERIODIC.UNIT_OF_TIME_IDX,unit);
            dobj.addRow(periodicRow);
            Row trafficSch = new Row("Traffic_Schedule");
            trafficSch.set("TENANT_ID", tenant_id);
            trafficSch.set("COUNT", count);
            pers.add(dobj);
            Scheduler s = (Scheduler)BeanUtil.lookup("Scheduler");
            DataObject dobj1 = pers.constructDataObject();
            Row taskInputRow = new Row(TASK_INPUT.TABLE);
            dobj1.addRow(taskInputRow);
            s.scheduleTask(schedulename, startdate, dobj1);
            return true;
        } catch (Exception e) {
            logger.info(e.toString());
            e.printStackTrace();
        }
        return false;
    }
}
