package task;

import com.adventnet.taskengine.Task;
import com.adventnet.taskengine.TaskContext;
import com.adventnet.taskengine.TaskExecutionException;
import controller.MailTrafficGenerator;
import dao.ScheduleDao;
import exception.CredentialException;
import model.TaskSchedule;

public class MailTrafficTask implements Task {
    @Override
    public void executeTask(TaskContext task) throws TaskExecutionException {
        long scheduleId = task.getScheduleID();
        ScheduleDao sdao = ScheduleDao.getInstance();
        TaskSchedule ts = sdao.getScheduleById(scheduleId);
        MailTrafficGenerator mtg = new MailTrafficGenerator(ts.getTenantid(), (int)ts.getCount());
        try {
            mtg.generateTraffic();
        } catch (CredentialException e) {
            e.printStackTrace();
            throw new TaskExecutionException(e);
        }
    }
    @Override
    public void stopTask() throws TaskExecutionException {
        
    }
    
}
