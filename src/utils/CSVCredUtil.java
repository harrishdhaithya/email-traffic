package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.Credential;

public class CSVCredUtil implements ICredUtil {
    private String fileContent;
    private List<String> recievers = null;
    private List<Credential> credentials = null;
    public CSVCredUtil(String fileContent) {
        this.fileContent = fileContent;
        System.out.println(this.fileContent);
    }
    private synchronized List<Credential> getCredentials(){
        if(credentials==null){
            credentials = new ArrayList<>();
            String[] users = fileContent.split("\n");
            for(int i=1;i<users.length;i++){
                String user = users[i];
                System.out.println(user);
                String[] pair = user.split(",");
                String email = pair[0].trim();
                if(pair.length==2){
                    String password = pair[1].trim();
                    credentials.add(new Credential(email,password));
                }
            }
        }
        return credentials;
    }
    private synchronized List<String> getRecievers(){
        if(recievers==null){
            recievers = new ArrayList<>();
            String[] users = fileContent.split("\n");
            for(int i=1;i<users.length;i++){
                String user = users[i];
                String[] pair = user.split(",");
                String email = pair[0].trim();
                recievers.add(email);
            }
        }
        return recievers;
    }
    @Override
    public Credential getRandomCredPair(){
        Random r = new Random();
        List<Credential> creds = getCredentials();
        if(creds==null){
            return null;
        }
        int randInt = r.nextInt(creds.size());
        Credential c = creds.get(randInt);
        return creds.get(randInt);
    }
    @Override
    public String getRandomReciever(){
        Random r = new Random();
        List<String> receivers = getRecievers();
        if(receivers==null){
            return null;
        }
        System.out.println(receivers.toString());
        int randInt = r.nextInt(receivers.size());
        return receivers.get(randInt);
    }
}
