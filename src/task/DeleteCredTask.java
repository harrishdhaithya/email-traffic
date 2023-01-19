package task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import com.adventnet.taskengine.Task;
import com.adventnet.taskengine.TaskContext;
import com.adventnet.taskengine.TaskExecutionException;
import dao.CredentialDao;
import dao.TenantDao;
import model.Tenant;

public class DeleteCredTask implements Task {
    Logger logger = Logger.getLogger(this.getClass().getName());
    @Override
    public void executeTask(TaskContext arg0) throws TaskExecutionException {
        TenantDao tdao = TenantDao.getInstance();
        List<Tenant> tenants = tdao.getAllTenants();
        CredentialDao cdao = CredentialDao.getInstance();
        for(Tenant t:tenants){
            String[] emails = cdao.deleteNRandomEmail(t.getId(), 10);
            logger.info(Arrays.toString(emails));
            ProcessBuilder pb = new ProcessBuilder("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe","../webapps/mailtraffic/deleteMailboxes.ps1","-emails",String.join(",", emails))
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
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stopTask() throws TaskExecutionException { 
    }
    
}
