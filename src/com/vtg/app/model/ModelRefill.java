package com.vtg.app.model;

public class ModelRefill {
    public String amount;
    public String code;
    public String date;
    public String type;

    public ModelRefill(String date, String code, String type, String amount) {
        this.date = date;
        this.code = code;
        this.type = type;
        this.amount = amount;
    }
    public ModelRefill(){
    	
    }
}
