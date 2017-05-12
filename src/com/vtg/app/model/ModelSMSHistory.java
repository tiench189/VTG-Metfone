package com.vtg.app.model;

public class ModelSMSHistory {
    public String cost;
    public String date;
    public String number;

    public ModelSMSHistory(String date, String cost, String number) {
        this.date = date;
        this.cost = cost;
        this.number = number;
    }
    public ModelSMSHistory(){
    	
    }
}
