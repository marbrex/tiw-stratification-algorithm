package com.mif14;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DatalogProgram {

    private String content;
    private final String filePath;
    private final Pattern COMMENT = Pattern.compile("%.*", Pattern.MULTILINE);
    private final Pattern EDB = Pattern.compile("^(\\w*)\\(\\s*(((?:(?:(?:[a-z]\\w*)|(?:\\d+))\\s*)|(?:'[\\w\\s]+'\\s*))(?:,\\s*((?:(?:(?:[a-z]\\w*)|(?:\\d+))+\\s*)|(?:'[\\w\\s]+'\\s*)))*)\\s*\\)\\.", Pattern.MULTILINE);
    private final Pattern IDB = Pattern.compile("^(\\w*)\\(\\s*((?:(?:[A-Z]\\w*)+\\s*)(?:,\\s*(?:(?:[A-Z]\\w*)+\\s*))*)\\s*\\)\\s*:-\\s*(not\\s+|)?(\\w*)\\(\\s*((?:(?:\\w+\\s*)|(?:'[\\w\\s]+'\\s*))(?:,\\s*(?:(?:\\w+\\s*)|(?:'[\\w\\s]+'\\s*)))*)\\s*\\)\\s*(?:,\\s*(?:(not\\s+|)?(\\w*)\\(\\s*((?:(?:(?:[a-zA-Z]\\w*)+\\s*)|(?:'[\\w\\s]+'\\s*))(?:,\\s*(?:(?:(?:[a-zA-Z]\\w*)+\\s*)|(?:'[\\w\\s]+'\\s*)))*)\\s*\\)))*\\s*\\.", Pattern.MULTILINE);
    private final List<Fact> facts = new ArrayList<>();
    private final List<Rule> rules = new ArrayList<>();
    private final Map<Predicate, Integer> stratum = new HashMap<>();

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
            content = scanner.next();

            final Matcher matcherEDB = EDB.matcher(content);
            while (matcherEDB.find()) {
                // System.out.println("Full match: " + matcherEDB.group(0));

                // for (int i = 1; i <= matcherEDB.groupCount(); i++) {
                //     System.out.println("Group " + i + ": " + matcherEDB.group(i));
                // }

                String name = matcherEDB.group(1);
                String[] params = matcherEDB.group(2).split(",");
                List<String> paramsList = Arrays.stream(params).map(String::trim).collect(Collectors.toList());
                // paramsList.forEach(System.out::println);

                Fact f = new Fact(name, paramsList);
                f.text = matcherEDB.group(0);
                facts.add(f);
            }

            // final Matcher matcherCOMMENT = COMMENT.matcher(content);
            // while (matcherCOMMENT.find()) {
            //     System.out.println("Full match: " + matcherCOMMENT.group(0));
            // }

            final Matcher matcherIDB = IDB.matcher(content);
            while (matcherIDB.find()) {
                // System.out.println("Full match: " + matcherIDB.group(0));

                String name = matcherIDB.group(1);
                String[] params = matcherIDB.group(2).split(",");
                List<Predicate.Parameter> paramsList = Arrays.stream(params).map(p -> {
                    return new Predicate.MutableParameter(p.trim());
                }).collect(Collectors.toList());

                Predicate ruleHead = new Predicate(name,paramsList,false,true);
                Rule r = new Rule(ruleHead);
                r.text = matcherIDB.group(0);

                boolean isNegated = false;
                String predName = "";
                List<Predicate.Parameter> predParams;
                String current;
                for (int i = 3; i <= matcherIDB.groupCount(); i++) {
                    // System.out.println("Group " + i + ": " + matcherIDB.group(i));

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

        rules.forEach(rule -> {
            stratum.put(rule.getHead(), 1);
            rule.getBody().forEach(p -> stratum.put(p, 1));
        });

        AtomicBoolean stratumChanged = new AtomicBoolean(false);
        AtomicBoolean stratumExceeds = new AtomicBoolean(false);
        do {
            stratumChanged.set(false);
            stratumExceeds.set(false);
            rules.forEach(r -> {
                Predicate p = r.getHead();
                r.getBody().forEach(q -> {
                    Integer strataQ = stratum.get(q);
                    Integer value = q.isNegated() ? ++strataQ : strataQ;
                    if (value > stratum.get(p)) {
                        stratumChanged.set(true);
                        if (value <= rules.size()) stratum.put(p, value);
                        else stratumExceeds.set(true);
                    }
                });
            });
        } while(stratumChanged.get() && !stratumExceeds.get());

        // rules.forEach(r -> System.out.println(r.getHead().getName() + " : " + stratum.get(r.getHead())));
    }

    public void output() {

        SortedMap<Integer, List<Rule>> partitions = new TreeMap<>();

        stratum.forEach(((predicate, strata) -> {
            if (predicate.isRuleHead()) {
                Rule rule = null;
                for (Rule r : rules) {
                    if (r.getHead().equals(predicate)) {
                        rule = r;
                        break;
                    }
                }
                List<Rule> part = partitions.get(strata);
                if (part == null) part = new ArrayList<>();
                part.add(rule);
                partitions.put(strata, part);
            }
        }));

        StringBuilder builder = new StringBuilder();
        partitions.forEach((strata, part) -> {
            builder.append("P").append(strata).append(" = {\n");
            part.forEach(rule -> {
                builder.append("\t").append(rule.text).append("\n");
            });
            builder.append("}\n");
        });

        System.out.println(builder.toString());
    }

}
