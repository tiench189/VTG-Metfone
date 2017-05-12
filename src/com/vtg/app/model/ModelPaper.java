package com.vtg.app.model;

public class ModelPaper {
    public String date;
    public String no;
    public String place;
    public String type;

    public ModelPaper(String no, String type, String date, String place) {
        this.no = no;
        this.type = type;
        this.date = date;
        this.place = place;
    }
}
