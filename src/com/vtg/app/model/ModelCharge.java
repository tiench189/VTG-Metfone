package com.vtg.app.model;

public class ModelCharge {
    public String amount;
    public String date;
    public String duration;
    public String type;
    public String number;

    public ModelCharge(String date, String type, String duration, String amount, String number) {
        this.date = date;
        this.type = type;
        this.duration = duration;
        this.amount = amount;
        this.number = number;
    }
    public ModelCharge(){
    	
    }
}
