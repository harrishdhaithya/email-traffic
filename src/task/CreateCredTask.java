package task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import com.adventnet.taskengine.Task;
import com.adventnet.taskengine.TaskContext;
import com.adventnet.taskengine.TaskExecutionException;
import dao.ConfigDao;
import dao.CredentialDao;
import dao.TenantDao;
import model.Credential;
import model.Tenant;

public class CreateCredTask implements Task {
    private static Logger logger = Logger.getLogger(CreateCredTask.class.getName());
    private static String getRandomDigit(int digit){
        String num = "";
        for(int i=0;i<10;i++){
            Random rand = new Random();
            int n = rand.nextInt(10);
            num+=n;
        }
        return num;
    }
    private static String CreateRandomEmail(Tenant t){
        String prefix = "testing_mailbox_";
        String random = getRandomDigit(10);
        return prefix+random;
    }
    private List<String> getEmails(Tenant t,int count){
        List<String> emails = new ArrayList<>(count);
        for(int i=0;i<count;i++){
            String email = CreateRandomEmail(t);
            emails.add(email);
        }
        return emails;
    }
    private List<Credential> formCredList(List<String> names,Tenant t,String password){
        List<Credential> credentials = new ArrayList<>(names.size());
        for(String email: names){
            credentials.add(new Credential(email+"@"+t.getName(),password,t.getId()));
        }
        return credentials;
    }
    @Override
    public void executeTask(TaskContext arg0) throws TaskExecutionException {
        ConfigDao confdao = ConfigDao.getInstance();
        String scriptpath = confdao.getConfig("scriptpath").getPropValue();
        TenantDao tdao = TenantDao.getInstance();
        CredentialDao cdao = CredentialDao.getInstance();
        List<Tenant> tenants = tdao.getAllTenants();
        for(Tenant t:tenants){
            List<String> emails = getEmails(t,10);
            ProcessBuilder pb = new ProcessBuilder("C:\\Windows\\System32\\WindowsPowerShell\\v1.0\\powershell.exe",scriptpath,"-emails",String.join(",", emails),"-adminemail",t.getAdminEmail(),"-adminpassword",t.getAdminPassword(),"-operation","create")
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
                List<Credential> creds = formCredList(emails,t, "m365password@123");
                boolean success = cdao.addCredentials(creds);
                if(!success){
                    logger.warning("Failed to add Credential...");
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
