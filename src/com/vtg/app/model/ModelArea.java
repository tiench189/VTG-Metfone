package com.vtg.app.model;

public class ModelArea {
    public String code;
    public String district;
    public String name;

    public ModelArea(String code, String name) {
        this.district = "";
        this.code = code;
        this.name = name;
    }

    public ModelArea() {
        this.district = "";
    }
}
