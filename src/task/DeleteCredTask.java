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
    Logger logger = Logger.getLogger(this.getClass().getName());
    @Override
    public void executeTask(TaskContext arg0) throws TaskExecutionException {
        ConfigDao confdao = ConfigDao.getInstance();
        Config conf = confdao.getConfig("autouserdelete");
        if(conf==null||conf.getPropValue().equals("false")){
            return;
        }
        TenantDao tdao = TenantDao.getInstance();
        List<Tenant> tenants = tdao.getAllTenants();
        CredentialDao cdao = CredentialDao.getInstance();
        for(Tenant t:tenants){
            String[] emails = cdao.deleteNRandomEmail(t.getId(), 10);
            ProcessBuilder pb = new ProcessBuilder("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe","../webapps/mailtraffic/deleteMailboxes.ps1","-emails",String.join(",", emails),"-adminemail",t.getAdminEmail(),"-adminpassword",t.getAdminPassword())
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
