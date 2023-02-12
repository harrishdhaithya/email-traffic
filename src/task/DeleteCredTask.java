package task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Logger;
import com.adventnet.taskengine.Task;
import com.adventnet.taskengine.TaskContext;
import com.adventnet.taskengine.TaskExecutionException;
import dao.ConfigDao;
import dao.CredentialDao;
import dao.TenantDao;
import model.Config;
import model.Tenant;

public class DeleteCredTask implements Task {
    private static Logger logger = Logger.getLogger(DeleteCredTask.class.getName());
    @Override
    public void executeTask(TaskContext arg0) throws TaskExecutionException {
        ConfigDao confdao = ConfigDao.getInstance();
        // String powerShellPath = confdao.getConfig("powershell").getPropValue();
        String scriptpath = confdao.getConfig("scriptpath").getPropValue();
        TenantDao tdao = TenantDao.getInstance();
        List<Tenant> tenants = tdao.getAllTenants();
        CredentialDao cdao = CredentialDao.getInstance();
        for(Tenant t:tenants){
            long count = cdao.getCredCount(t.getId());
            logger.info(count+"");
            if(count>=100){
                String[] emails = cdao.getNRandomEmails(t.getId(), 10);
                ProcessBuilder pb = new ProcessBuilder("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe",scriptpath,"-emails",String.join(",", emails),"-adminemail",t.getAdminEmail(),"-adminpassword",t.getAdminPassword(),"-operation","delete")
                .redirectInput(ProcessBuilder.Redirect.INHERIT) 
                .redirectOutput(ProcessBuilder.Redirect.INHERIT) 
                .redirectError(ProcessBuilder.Redirect.INHERIT);
                try {
                    Process proc = pb.start();
                    InputStream is = proc.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line = "";
                    while((line=br.readLine())!=null){
                        logger.info(line);
                    }
                    boolean success = cdao.deleteMailboxes(emails);
                    if(!success){
                        logger.warning("Failed to update deleted mails in db...");
                        throw new TaskExecutionException("Failed to update deleted mails in db...");
                    }
                    logger.warning("Successfully Deleted Mails");
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    throw new TaskExecutionException(e);
                }
            }
            
        }
    }

    @Override
    public void stopTask() throws TaskExecutionException { 
    }
    
}
