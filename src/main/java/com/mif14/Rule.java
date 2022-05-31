package com.mif14;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Rule {

    public final static String RULE_DELIMITER = ":-";
    public final static String BODY_DELIMITER = ",";

    private final String rule;
    private String headText;
    private String bodyText;
    private Tuple head;
    private final List<Tuple> body = new ArrayList<>();

    public Rule(String rule) throws InvalidInputFileException {
        if (rule != null && !"".equals(rule)) {
            this.rule = rule;
            this.parseRule();
        }
        else throw new InvalidInputFileException();
    }

    public String getAsText() {
        return rule;
    }

    public Tuple getHead() {
        return head;
    }

    public List<Tuple> getBody() {
        return body;
    }

    private void parseRule() throws InvalidInputFileException {
        Scanner scanner = new Scanner(rule);
        scanner.useDelimiter(RULE_DELIMITER);

        this.headText = scanner.hasNext() ? scanner.next().trim() : "";
        this.bodyText = scanner.hasNext() ? scanner.next().trim() : "";

        if ("".equals(headText) && "".equals(bodyText)) {
            throw new InvalidInputFileException();
        }
        else {
            this.head = new Tuple(headText);
            this.parseBody();
        }
    }

    private void parseBody() throws InvalidInputFileException {
        Scanner scanner = new Scanner(bodyText);
        scanner.useDelimiter(BODY_DELIMITER);

        while (scanner.hasNext()) {

            String tupleText = scanner.next().trim();

            if ("".equals(tupleText)) {
                throw new InvalidInputFileException();
            }
            else {
                Tuple tuple = new Tuple(tupleText);
                body.add(tuple);
            }
        }
    }

}
