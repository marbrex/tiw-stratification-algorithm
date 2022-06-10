package com.mif14;

import java.io.FileNotFoundException;

public class App {

    public static void main(String[] args) {

        try {
            DatalogProgram dp = new DatalogProgram("input.txt");
            dp.test();

        } catch (FileNotFoundException | InvalidInputFileException e) {
            throw new RuntimeException(e);
        }
    }
}
