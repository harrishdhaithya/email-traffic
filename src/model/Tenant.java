package model;

public class Tenant {
    private long id;
    private String name;
    public Tenant(String name){
        this.name = name;
    }
    public Tenant(long id, String name){
        this.id = id;
        this.name = name;
    }
    public void setId(long id){
        this.id = id;
    }
    public void setName(String name){
        this.name = name;
    }
    public long getId(){
        return id;
    }
    public String getName(){
        return name;
    }
}
