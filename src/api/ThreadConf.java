package api;

import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import dao.ConfigDao;
import model.Config;

public class ThreadConf extends HttpServlet {
    Logger logger = Logger.getLogger(this.getClass().getName());
    private static boolean isNumeric(String s){
        boolean numeric = true;
        for(int i=0;i<s.length();i++){
            numeric = numeric&&Character.isDigit(s.charAt(i));
        }
        return numeric;
    }
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        PrintWriter out = null;
        JSONObject jobj = new JSONObject();
        try{
            out = resp.getWriter();
            String threadpool = req.getParameter("poolsize");
            if(threadpool==null){
                throw new Exception("Plese Enter poolsize...");
            }
            if(!isNumeric(threadpool)){
                throw new Exception("Enter a numeric value for Thread Pool Size...");
            }
            ConfigDao cdao = ConfigDao.getInstance();
            Config conf = new Config("poolsize", threadpool);
            boolean success = cdao.updateConfig(conf);
            if(!success){
                throw new Exception("Not able to Update...");
            }
            resp.setStatus(200);
            resp.setContentType("application/json");
            jobj.put("message", "Updated Successfully");
            out.println(jobj.toString());
        }catch(Exception ex){
            resp.setStatus(400);
            // resp.setContentType("application/json");
            logger.warning(ex.toString());
            // jobj.put("error", ex.getMessage());
            out.println(ex.getMessage());
        }
    }
}
