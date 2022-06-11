package com.mif14;

import java.util.ArrayList;
import java.util.List;

public class Rule {

    public String text;
    private final Predicate head;
    private final List<Predicate> body = new ArrayList<>();

    public Rule(Predicate head) {
        this.head = head;
    }

    public Rule(Predicate head, List<Predicate> body) {
        this.head = head;
        this.body.addAll(body);
    }

    public List<Predicate> getBody() {
        return body;
    }

    public Predicate getPredicate(int i) {
        return body.get(i);
    }

    public void addPredicate(Predicate pred) {
        this.body.add(pred);
    }

    public Predicate getHead() {
        return head;
    }
}
