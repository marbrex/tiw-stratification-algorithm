package com.mif14;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EDB {

    public final static String TUPLES_DELIMITER = ",";

    private String EDB;
    private final List<Tuple> tuples = new ArrayList<>();

    public EDB(String EDB) throws InvalidInputFileException {
        if (EDB != null && !"".equals(EDB)) {
            this.EDB = EDB;
            this.parseBody();
        }
        else throw new InvalidInputFileException();
    }

    public String getAsText() {
        return EDB;
    }

    public List<Tuple> getTuples() {
        return tuples;
    }

    private void parseBody() throws InvalidInputFileException {
        Scanner scanner = new Scanner(EDB);
        scanner.useDelimiter(TUPLES_DELIMITER);

        while (scanner.hasNext()) {

            String tupleText = scanner.next().trim();

            if ("".equals(tupleText)) {
                throw new InvalidInputFileException();
            }
            else {
                Tuple tuple = new Tuple(tupleText);
                tuples.add(tuple);
            }
        }
    }
}
