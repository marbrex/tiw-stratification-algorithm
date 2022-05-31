package com.mif14;

import java.io.File;
import java.io.FileNotFoundException;
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

}