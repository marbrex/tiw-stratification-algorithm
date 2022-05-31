package com.mif14;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class DatalogProgram {

    public final static String DATA_DELIMITER = "/";

    private IDB IDB;
    private EDB EDB;
    private final String filePath;

    public DatalogProgram(String filePath) throws FileNotFoundException, InvalidInputFileException {
        if (filePath != null && !"".equals(filePath)) {
            this.filePath = filePath;
            this.loadInput();
        }
        else throw new InvalidInputFileException();
    }

    public String getIDBasText() {
        return IDB.getAsText();
    }

    public String getEDBasText() {
        return EDB.getAsText();
    }

    public com.mif14.IDB getIDB() {
        return IDB;
    }

    public com.mif14.EDB getEDB() {
        return EDB;
    }

    private void loadInput() throws FileNotFoundException, InvalidInputFileException {
        Scanner scanner = new Scanner(new File(filePath));
        scanner.useDelimiter(DATA_DELIMITER);

        String textIDB = scanner.hasNext() ? scanner.next() : "";
        String textEDB = scanner.hasNext() ? scanner.next() : "";

        if ("".equals(textIDB) && "".equals(textEDB)) {
            throw new InvalidInputFileException();
        }
        else {
            IDB = new IDB(textIDB.trim());
            EDB = new EDB(textEDB.trim());
        }

        scanner.close();
    }

    public void output() {
        System.out.println("\nIDB:");
        getIDB().getRules().forEach(r -> {
            System.out.print(r.getHead().getHead() + "(" + r.getHead().getAttributesText() + ") :- ");

            List<Tuple> body = r.getBody();
            for (int k = 0, bodySize = body.size(); k < bodySize; k++) {
                Tuple t = body.get(k);
                System.out.print(t.getHead() + "(");

                List<String> attributes = t.getAttributes();
                for (int j = 0, attributesSize = attributes.size(); j < attributesSize; j++) {
                    String s = attributes.get(j);
                    System.out.print(s);
                    if (j < attributesSize - 1) System.out.print(";");
                }

                System.out.print(")");
                if (k < bodySize - 1) System.out.print(", ");
            }

            System.out.println();
        });

        System.out.println("\nEDB:");
        getEDB().getTuples().forEach(t -> {
            System.out.print(t.getHead() + "(");

            List<String> attributes = t.getAttributes();
            for (int i = 0, attributesSize = attributes.size(); i < attributesSize; i++) {
                String s = attributes.get(i);
                System.out.print(s);
                if (i < attributesSize - 1) System.out.print(";");
            }

            System.out.println(")");
        });
    }

    public void stratify() {

    }

}
