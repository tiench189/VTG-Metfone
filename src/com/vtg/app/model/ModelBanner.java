package com.vtg.app.model;

public class ModelBanner {
    public String action;
    public String id;
    public String image;
    public String status;

    public ModelBanner(String id, String image, String action, String status) {
        this.id = id;
        this.image = image;
        this.action = action;
        this.status = status;
    }
    
    public ModelBanner(){
    	
    }
}
