package api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.json.JSONArray;
import org.json.JSONObject;
import dao.CredentialDao;
import model.Credential;

@MultipartConfig
public class CredentialServ extends HttpServlet {
    Logger logger = Logger.getLogger(this.getClass().getName());
    private List<Credential> getCredentials(String content,long tenantId){
        List<Credential> credentials = new ArrayList<>();
        String[] users = content.split("\n");
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
            CredentialDao cdao = CredentialDao.getInstance();
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
            InputStream is = p.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while((line=br.readLine())!=null){
                sb.append(line+"\n");
            }
            List<Credential> credentials = getCredentials(sb.toString(), Long.parseLong(tenantId));
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
            logger.warning(ex.toString());
            resp.setStatus(400);
            out.println(ex.getMessage());
       }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        String lowerBound = req.getParameter("lowerbound");
        String tenantid = req.getParameter("tenant");
        String count = req.getParameter("count");
        PrintWriter out = null;
        try{
            out = resp.getWriter();
            int lower = 1;
            int countNum = 100;
            if(lowerBound != null){
                lower = Integer.parseInt(lowerBound);
            }
            if(count != null){
                countNum = Integer.parseInt(count);
            }
            int tenant = Integer.parseInt(tenantid);
            CredentialDao cdao = CredentialDao.getInstance();
            JSONObject result = new JSONObject();
            long totaldata = cdao.getCredCount(tenant);
            result.put("rangestart", lower);
            List<Credential> cred = cdao.getCredentials(tenant, lower, countNum);
            result.put("rangeend", lower+cred.size());
            result.put("totalcount", totaldata);
            JSONArray jarr = new JSONArray();
            for(Credential credential:cred){
                JSONObject jobj = new JSONObject();
                jobj.put("id", credential.getId());
                jobj.put("email", credential.getEmail());
                jobj.put("status",credential.getStatus());
                jarr.put(jobj);
            }
            result.put("data", jarr);
            resp.setStatus(200);
            resp.setContentType("application/json");
            out.println(result.toString());
        }catch(NumberFormatException nex){
            logger.warning(nex.getMessage());
            nex.printStackTrace();
            resp.setStatus(400);
            out.println("Illegal Format...");
        }catch(NullPointerException nullex){
            logger.warning(nullex.getMessage());
            nullex.printStackTrace();
            resp.setStatus(400);
            out.println("Please Enter all the required fields...");
        }catch(Exception ex){
            logger.warning(ex.getMessage());
            ex.printStackTrace();
            resp.setStatus(400);
            out.println("Not able to get Credentials");
        }
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp){
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        CredentialDao cdao = CredentialDao.getInstance();
        PrintWriter out = null;
        try {
            out = resp.getWriter();
            if(password==null){
                resp.setStatus(400);
                out.println("All the fields are required...");
                return;
            }
            boolean success = cdao.updateCredentials(email, password);
            if(!success){
                resp.setStatus(400);
                out.println("Not able to update credential...");
                return;
            }
            resp.setStatus(200);
            JSONObject jobj = new JSONObject();
            jobj.put("message", "Updated Successfully...");
            resp.setContentType("application/json");
            out.println(jobj.toString());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(400);
            out.println("Not able to update credential...");
            return;
        }
        
    }
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp){
        String id = req.getParameter("id");
        PrintWriter out = null;
        try{
            out = resp.getWriter();
            if(id==null){
                resp.setStatus(400);
                out.println("Invalid Field...");
                return;
            }
            CredentialDao cdao = CredentialDao.getInstance();
            boolean success = cdao.deleteCred(Long.parseLong(id));
            if(!success){
                throw new Exception();
            }
            JSONObject resObj = new JSONObject();
            resObj.put("message", "Deleted Successfully...");
            resp.setStatus(200);
            resp.setContentType("application/json");
            out.println(resObj.toString());
        }catch(Exception ex){
            resp.setStatus(400);
            out.println("Not able to delete User...");
        }
    }
    
}