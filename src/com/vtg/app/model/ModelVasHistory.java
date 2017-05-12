package com.vtg.app.model;

public class ModelVasHistory {
    public String amount;
    public String date;
    public String service;
    public String type;

    public ModelVasHistory(String date, String type, String service, String amount) {
        this.date = date;
        this.type = type;
        this.service = service;
        this.amount = amount;
    }
    public ModelVasHistory(){
    	
    }
}
