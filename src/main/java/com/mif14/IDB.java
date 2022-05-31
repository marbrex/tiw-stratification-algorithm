package com.mif14;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class IDB {

    public final static String RULES_DELIMITER = "\n";

    private final String IDB;
    private final List<Rule> rules = new ArrayList<>();

    public IDB(String IDB) throws InvalidInputFileException {
        if (IDB != null && !"".equals(IDB)) {
            this.IDB = IDB;
            this.parse();
        }
        else throw new InvalidInputFileException();
    }

    public String getAsText() {
        return IDB;
    }

    public List<Rule> getRules() {
        return rules;
    }

    private void parse() throws InvalidInputFileException {
        Scanner scanner = new Scanner(IDB);
        scanner.useDelimiter(RULES_DELIMITER);

        while (scanner.hasNext()) {
            String ruleText = scanner.next().trim();
            Rule r = new Rule(ruleText);
            rules.add(r);
        }
    }
}
