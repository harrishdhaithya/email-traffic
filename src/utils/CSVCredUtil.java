package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;

public class CSVCredUtil implements ICredUtil {
    private String filepath;
    private List<String> recievers = null;
    private List<ExchangeCredentials> credentials = null;
    private String fileContent = null;
    public CSVCredUtil(String filepath) {
        this.filepath = filepath;
    }
    private String readFile(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.filepath));
            String line = "";
            StringBuilder content = new StringBuilder();
            String header = reader.readLine();
            while ((line = reader.readLine())!=null) {
                content.append(line+"\n");
            }
            reader.close();
            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private synchronized String getFileContent(){
        if(fileContent==null){
            fileContent = readFile();
        }
        return fileContent;
    }
    private synchronized List<ExchangeCredentials> getCredentials(){
        if(credentials==null){
            credentials = new ArrayList<>();
            String fileContent = getFileContent();
            String[] users = fileContent.split("\n");
            for(String user: users){
                String[] pair = user.split(",");
                String email = pair[0].trim();
                if(pair.length==2){
                    String password = pair[1].trim();
                    credentials.add(new WebCredentials(email,password));
                }
            }
        }
        return credentials;
    }
    private synchronized List<String> getRecievers(){
        if(recievers==null){
            recievers = new ArrayList<>();
            String fileContent = getFileContent();
            String[] users = fileContent.split("\n");
            for(String user:users){
                String[] pair = user.split(",");
                String email = pair[0].trim();
                if(pair.length==1||pair==null||pair[1].trim()==""){
                    recievers.add(email);
                }
            }
        }
        return recievers;
    }
    @Override
    public ExchangeCredentials getRandomCredPair(){
        Random r = new Random();
        List<ExchangeCredentials> creds = getCredentials();
        if(creds==null){
            return null;
        }
        int randInt = r.nextInt(creds.size());
        return creds.get(randInt);
    }
    @Override
    public String getRandomReciever(){
        Random r = new Random();
        List<String> receivers = getRecievers();
        if(receivers==null){
            return null;
        }
        int randInt = r.nextInt(receivers.size());
        return receivers.get(randInt);
    }
}
