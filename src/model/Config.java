package model;

public class Config {
    private long id;
    private String prop_name;
    private String prop_value;
    public Config(String prop_name,String prop_value){
        this.prop_name=prop_name;
        this.prop_value=prop_value;
    }
    public Config(long id,String prop_name,String prop_value){
        this.id = id;
        this.prop_name = prop_name;
        this.prop_value = prop_value;
    }
    public long getId(){
        return id;
    }
    public void setId(long id){
        this.id=id;
    }
    public String getPropName(){
        return prop_name;
    }
    public void setPropName(String prop_name){
        this.prop_name = prop_name;
    }
    public String getPropValue(){
        return prop_value;
    }
    public void setPropValue(String prop_value){
        this.prop_value = prop_value;
    }
}
