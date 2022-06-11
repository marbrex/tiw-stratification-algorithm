package com.mif14;

import java.util.ArrayList;
import java.util.List;

public class Predicate {

    private final String name;
    private final List<Parameter> params = new ArrayList<>();
    private final boolean isNegated;
    private final boolean isRuleHead;

    public Predicate(String name, boolean isNegated) {
        this.name = name;
        this.isNegated = isNegated;
        this.isRuleHead = false;
    }

    public Predicate(String name, List<Parameter> params, boolean isNegated) {
        this.name = name;
        this.params.addAll(params);
        this.isNegated = isNegated;
        this.isRuleHead = false;
    }

    public Predicate(String name, List<Parameter> params, boolean isNegated, boolean isRuleHead) {
        this.name = name;
        this.params.addAll(params);
        this.isNegated = isNegated;
        this.isRuleHead = isRuleHead;
    }

    public boolean isRuleHead() {
        return isRuleHead;
    }

    public String getName() {
        return name;
    }

    public boolean isNegated() {
        return isNegated;
    }

    public void setParam(int i, Parameter param) {
        this.params.set(i, param);
    }

    public Parameter getParam(int i) {
        return params.get(i);
    }

    abstract static class Parameter {

        protected String label;
        protected boolean isVariable;
        protected String value;

        public Parameter(String label, boolean isVariable, String value) {
            this.label = label;
            this.isVariable = isVariable;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public boolean isVariable() {
            return isVariable;
        }

        public String getValue() {
            return value;
        }
    }

    static class MutableParameter extends Parameter {

        public MutableParameter(String label) {
            super(label, true, null);
        }

        public MutableParameter(String label, String value) {
            super(label, true, value);
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    static class ImmutableParameter extends Parameter {

        public ImmutableParameter(String label, String value) {
            super(label, false, value);
        }
    }

}
