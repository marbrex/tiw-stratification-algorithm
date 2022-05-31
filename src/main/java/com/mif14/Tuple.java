package com.mif14;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Tuple {

    public final static String ATTRIBUTES_START = "(";
    public final static String ATTRIBUTES_END = ")";
    public final static char TUPLE_NEGATIVE = '!';
    public final static String ATTRIBUTES_DELIMITER = ";";

    private final String tuple;
    private String head;
    private boolean isNegative = false;
    private String attributesText;
    private final List<String> attributes = new ArrayList<>();

    public Tuple(String tuple) throws InvalidInputFileException {
        if (tuple != null && !"".equals(tuple)) {
            this.tuple = tuple;
            this.parse();
        }
        else throw new InvalidInputFileException();
    }

    public String getHead() {
        return head;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public String getAttributesText() {
        return attributesText;
    }

    public boolean isNegative() {
        return isNegative;
    }

    private void parse() throws InvalidInputFileException {
        int indexStartAttr = tuple.indexOf(ATTRIBUTES_START);
        int indexEndAttr = tuple.indexOf(ATTRIBUTES_END);

        head = tuple.substring(0, indexStartAttr);
        if (head.charAt(0) == TUPLE_NEGATIVE) {

            if (head.length() > 1) {
                isNegative = true;
                head = head.substring(1);
            }
            else {
                throw new InvalidInputFileException();
            }
        }

        attributesText = tuple.substring(indexStartAttr + 1, indexEndAttr);
        Scanner scanner = new Scanner(attributesText);
        scanner.useDelimiter(ATTRIBUTES_DELIMITER);

        while (scanner.hasNext()) {

            String attribute = scanner.next().trim();

            if ("".equals(attribute)) {
                throw new InvalidInputFileException();
            }
            else {
                attributes.add(attribute);
            }
        }
    }

}
