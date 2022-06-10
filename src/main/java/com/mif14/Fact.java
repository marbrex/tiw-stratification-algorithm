package com.mif14;

import java.util.ArrayList;
import java.util.List;

public class Fact {

    private final String name;
    private final List<String> params = new ArrayList<>();
    public String text;

    public Fact(String name, List<String> params) {
        this.name = name;
        this.params.addAll(params);
    }

    public void addParam(String p) {
        params.add(p);
    }

    public String getParam(int i) {
        return params.get(i);
    }

    public String getName() {
        return name;
    }
}
