package api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.json.JSONObject;
import dao.CredentialDao;
import model.Credential;
import singleton.Singleton;

@MultipartConfig
public class UploadCreds extends HttpServlet {
    Logger logger = Logger.getLogger(this.getClass().getName());
    private String readFile(String filename){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line = "";
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine())!=null) {
                content.append(line+"\n");
            }
            reader.close();
            return content.toString();
        } catch (Exception e) {
            // e.printStackTrace();
            logger.warning(e.toString());
        }
        return null;
    }
    private List<Credential> getCredentials(String filename,long tenantId){
        List<Credential> credentials = new ArrayList<>();
        String fileContent = readFile(filename);
        String[] users = fileContent.split("\n");
        for(String user: users){
            String[] pair = user.split(",");
            String email = pair[0].trim();
            String password = (pair.length==1||pair[1].trim()=="")?null:pair[1];
            credentials.add(new Credential(email,password,tenantId));
        }
        return credentials;
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp){
       PrintWriter out = null;
       try{
            out = resp.getWriter();
            String tenantId = req.getParameter("tenantid");
            CredentialDao cdao = Singleton.getCredentialDao();
            Part p = req.getPart("file");
            if(
                p==null 
            ){
                throw new Exception("Please upload csv file");
            }
            if(!p.getContentType().equals("text/csv")){
                throw new Exception("Incorrect Format...");
            }
            if(tenantId==null){
                throw new Exception("The value of tenant id is required...");
            }
            String fileName = "E:\\AdventNet\\MickeyLite\\files\\" + p.getSubmittedFileName();
            p.write(fileName);
            List<Credential> credentials = getCredentials(fileName, Long.parseLong(tenantId));
            boolean success = cdao.addCredentials(credentials);
            if(success){
                JSONObject jobj = new JSONObject();
                jobj.put("message", "Credentials added successfully...");
                resp.setStatus(200);
                resp.setContentType("application/json");
                out.println(jobj.toString());
                return;
            }else{
                throw new Exception("Something went wrong...");
            }
       }catch(Exception ex){
            JSONObject jobj = new JSONObject();
            // ex.printStackTrace();
            logger.warning(ex.toString());
            jobj.put("error", ex.getMessage());
            resp.setStatus(400);
            resp.setContentType("application/json");
            out.println(jobj.toString());
       }
    }
}