package com.mif14;

import java.io.FileNotFoundException;

public class App {

    public static void main(String[] args) {

        try {
            DatalogProgram dp = new DatalogProgram("input.txt");
            System.out.println("\nIDB: \n" + dp.getIDBasText());
            System.out.println("\nEDB: \n" + dp.getEDBasText());

            System.out.println("\nIDB Rules:");
            dp.getIDB().getRules().forEach(r -> System.out.println(r.getAsText()));

        } catch (FileNotFoundException | InvalidInputFileException e) {
            throw new RuntimeException(e);
        }
    }
}
