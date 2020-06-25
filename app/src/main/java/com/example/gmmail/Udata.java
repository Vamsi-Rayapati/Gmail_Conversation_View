package com.example.gmmail;

public class Udata {
    private String name;
    private String snippet;
    private  String email;
    public Udata(String n,String snippet,String email)
    {
        this.name=n;
        this.snippet= snippet;
        this.email=email;
    }

    public String getName() {
        return name;
    }
    public String getSnippet(){
        return snippet;
    }
    public String getEmail(){
        return  email;
    }
}
