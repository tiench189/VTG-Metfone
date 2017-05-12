package com.vtg.app.model;

public class ModelVas {
    public String code;
    public String description;
    public String fee;
    public String guide;
    public String id;
    public String name;
    public String receiverIsdn;
    public String regis;
    public String register_date;
    public String remove;
    public int status;

    public ModelVas(String id, String name, String code, String description, String guide, String receiverIsdn, int status, String fee) {
        this.register_date = "";
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.guide = guide;
        this.status = status;
        this.receiverIsdn = receiverIsdn;
        this.fee = fee;
    }

    public ModelVas() {
        this.register_date = "";
    }
}
