package task;

import java.util.List;
import java.util.logging.Logger;
import com.adventnet.taskengine.Task;
import com.adventnet.taskengine.TaskContext;
import com.adventnet.taskengine.TaskExecutionException;
import dao.CredentialDao;
import dao.TenantDao;
import model.Tenant;

public class InactivateMBox implements Task {
    Logger logger = Logger.getLogger(InactivateMBox.class.getName());
    @Override
    public void executeTask(TaskContext arg0) throws TaskExecutionException {
        TenantDao tdao = TenantDao.getInstance();
        List<Tenant> tenants = tdao.getAllTenants();
        for(Tenant tenant:tenants){
            CredentialDao cdao = CredentialDao.getInstance();
            long count = cdao.getCredCount(tenant.getId());
            if(count>=100){
                boolean success = cdao.inactivateNMailBoxes(tenant.getId(), 10);
                if(!success){
                    logger.info("Not able to Inactivate MailBox");
                    throw new TaskExecutionException("Not able to Inactivate Mailboxes");
                }
                logger.info("Inactivated Successfully...");
            }
        }
    }
    @Override
    public void stopTask() throws TaskExecutionException {
    }
}