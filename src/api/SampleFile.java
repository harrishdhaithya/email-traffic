package api;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SampleFile extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp){
        PrintWriter out = null;
        try{
            FileReader reader = new FileReader("../webapps/mailtraffic/file/samplefile.csv");
            BufferedReader br = new BufferedReader(reader);
            OutputStream os = resp.getOutputStream();
            out = new PrintWriter(new OutputStreamWriter(os));
            StringBuilder content = new StringBuilder();
            String line = "";
            while ((line=br.readLine())!=null) {
                content.append(line+"\n");
            }
            resp.setStatus(200);
            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment; filename=\"userDirectory.csv\"");
            os.write(content.toString().getBytes());
            os.flush();
            os.close();
            br.close();
            reader.close();
        }catch(Exception ex){
            ex.printStackTrace();
            resp.setStatus(400);
            out.println("Not able to download sample file...");
        }
        
    }
    
}
