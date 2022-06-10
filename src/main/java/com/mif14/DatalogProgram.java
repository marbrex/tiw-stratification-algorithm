package com.mif14;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DatalogProgram {

    private final String filePath;
    private String content;
    private final Pattern COMMENT = Pattern.compile("%.*", Pattern.MULTILINE);
    private final Pattern EDB = Pattern.compile("^(\\w*)\\(\\s*(((?:(?:[a-z]\\w*)+\\s*)|(?:'[\\w\\s]+'\\s*))(?:,\\s*((?:(?:[a-z]\\w*)+\\s*)|(?:'[\\w\\s]+'\\s*)))*)\\s*\\)\\.", Pattern.MULTILINE);
    private final Pattern IDB = Pattern.compile("^(\\w*)\\(\\s*((?:(?:[A-Z]\\w*)+\\s*)(?:,\\s*(?:(?:[A-Z]\\w*)+\\s*))*)\\s*\\)\\s*:-\\s*(not\\s+|)?(\\w*)\\(\\s*((?:(?:\\w+\\s*)|(?:'[\\w\\s]+'\\s*))(?:,\\s*(?:(?:\\w+\\s*)|(?:'[\\w\\s]+'\\s*)))*)\\s*\\)(?:,\\s*(?:(not\\s+|)?(\\w*)\\(\\s*((?:(?:(?:[a-zA-Z]\\w*)+\\s*)|(?:'[\\w\\s]+'\\s*))(?:,\\s*(?:(?:(?:[a-zA-Z]\\w*)+\\s*)|(?:'[\\w\\s]+'\\s*)))*)\\s*\\)))*\\s*\\.", Pattern.MULTILINE);
    private final List<Fact> facts = new ArrayList<>();
    private final List<Rule> rules = new ArrayList<>();

    public DatalogProgram(String filePath) throws FileNotFoundException, InvalidInputFileException {
        if (filePath != null && !"".equals(filePath)) {
            this.filePath = filePath;
            this.load();
        }
        else throw new InvalidInputFileException();
    }

    private void load() {
        try {
            Scanner scanner = new Scanner(new File(filePath));
            scanner.useDelimiter("\\A");
            String all = scanner.next();

            final Matcher matcherEDB = EDB.matcher(all);
            while (matcherEDB.find()) {
                System.out.println("Full match: " + matcherEDB.group(0));

                for (int i = 1; i <= matcherEDB.groupCount(); i++) {
                    System.out.println("Group " + i + ": " + matcherEDB.group(i));
                }

                String name = matcherEDB.group(1);
                String[] params = matcherEDB.group(2).split(",");
                List<String> paramsList = Arrays.stream(params).map(String::trim).collect(Collectors.toList());
                paramsList.forEach(System.out::println);

                Fact f = new Fact(name, paramsList);
                f.text = matcherEDB.group(0);
                facts.add(f);
            }

            final Matcher matcherCOMMENT = COMMENT.matcher(all);
            while (matcherCOMMENT.find()) {
                System.out.println("Full match: " + matcherCOMMENT.group(0));
            }

            final Matcher matcherIDB = IDB.matcher(all);
            while (matcherIDB.find()) {
                System.out.println("Full match: " + matcherIDB.group(0));

                String name = matcherIDB.group(1);
                String[] params = matcherIDB.group(2).split(",");
                List<String> paramsList = Arrays.stream(params).map(String::trim).collect(Collectors.toList());

                Rule r = new Rule(name, paramsList);
                r.text = matcherIDB.group(0);

                boolean isNegated = false;
                String predName = "";
                List<Predicate.Parameter> predParams;
                String current;
                for (int i = 3; i <= matcherIDB.groupCount(); i++) {
                    System.out.println("Group " + i + ": " + matcherIDB.group(i));

                    if (matcherIDB.group(i) == null) break;

                    current = matcherIDB.group(i).trim();
                    int predIndx = i % 3;
                    if (predIndx == 0) {
                        // IS PREDICATE NEGATED
                        isNegated = "not".equals(current);
                    }
                    if (predIndx == 1) {
                        // PREDICATE NAME
                        predName = current;
                    }
                    if (predIndx == 2) {
                        // PREDICATE PARAMS
                        String[] predParamsAux = current.split(",");
                        predParams = Arrays.stream(predParamsAux).map(p -> {
                            String pT = p.trim();
                            if (Character.isUpperCase(pT.charAt(0))) {
                                return new Predicate.MutableParameter(pT);
                            }
                            else {
                                String value = pT;
                                if (pT.startsWith("'") && pT.endsWith("'")) value = pT.substring(1, pT.length()-1);
                                return new Predicate.ImmutableParameter(pT, value);
                            }
                        }).collect(Collectors.toList());

                        // CREATE PREDICATE
                        Predicate predicate = new Predicate(predName, predParams, isNegated);
                        r.addPredicate(predicate);
                    }
                }

                rules.add(r);
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void stratify() {

    }

    public void test() {
        // some tests
        System.out.println(rules.get(1).getPredicate(1).isNegated());
        System.out.println(rules.get(0).getPredicate(0).getParam(2).getLabel());
        System.out.println(rules.get(0).getPredicate(0).getParam(2).getValue());
        System.out.println(rules.get(0).getPredicate(0).getParam(2).isVariable());
        System.out.println(rules.get(0).getPredicate(0).getParam(1).isVariable());
        System.out.println(rules.get(2).getPredicate(0).getParam(1).isVariable());
    }

}
