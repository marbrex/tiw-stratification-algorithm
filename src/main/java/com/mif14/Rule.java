package com.mif14;

import java.util.ArrayList;
import java.util.List;

public class Rule {

    public String text;
    private final String name;
    private final List<String> params = new ArrayList<>();
    private final List<Predicate> predicates = new ArrayList<>();

    public Rule(String name, List<String> params) {
        this.name = name;
        this.params.addAll(params);
    }

    public Rule(String name, List<String> params, List<Predicate> predicates) {
        this.name = name;
        this.params.addAll(params);
        this.predicates.addAll(predicates);
    }

    public void setPredicate(int i, Predicate pred) {
        this.predicates.set(i, pred);
    }

    public Predicate getPredicate(int i) {
        return predicates.get(i);
    }

    public void addPredicate(Predicate pred) {
        this.predicates.add(pred);
    }
}
