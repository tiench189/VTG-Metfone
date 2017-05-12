package com.vtg.app.model;

import java.util.ArrayList;
import java.util.List;

public class ModelProvince {
    public String id;
    public String name;
    public List<ModelShowroom> showrooms;

    public ModelProvince(String id, String name) {
        this.showrooms = new ArrayList();
        this.id = id;
        this.name = name;
    }

    public ModelProvince() {
        this.showrooms = new ArrayList();
    }
}
