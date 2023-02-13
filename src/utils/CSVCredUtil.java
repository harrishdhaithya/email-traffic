package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import model.Credential;

public class CSVCredUtil implements ICredUtil {
    private String fileContent;
    private List<String> recievers = null;
    private List<Credential> credentials = null;
    // private String fileContent = null;
    public CSVCredUtil(String fileContent) {
        this.fileContent = fileContent;
        System.out.println(this.fileContent);
    }
    // private String readFile(){
    //     try {
    //         BufferedReader reader = new BufferedReader(new FileReader(this.filepath));
    //         String line = "";
    //         StringBuilder content = new StringBuilder();
    //         //header 
    //         reader.readLine();
    //         while ((line = reader.readLine())!=null) {
    //             content.append(line+"\n");
    //         }
    //         reader.close();
    //         return content.toString();
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return null;
    // }
    // private synchronized String getFileContent(){
    //     if(fileContent==null){
    //         fileContent = readFile();
    //     }
    //     return fileContent;
    // }
    private synchronized List<Credential> getCredentials(){
        if(credentials==null){
            credentials = new ArrayList<>();
            // String fileContent = getFileContent();
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
            // String fileContent = getFileContent();
            String[] users = fileContent.split("\n");
            for(int i=1;i<users.length;i++){
                String user = users[i];
                String[] pair = user.split(",");
                String email = pair[0].trim();
                recievers.add(email);
            }
            System.out.println("Rec: "+recievers.toString());
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
        System.out.println("Email: "+c.getEmail());
        System.out.println("Password: "+c.getPassword());
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
