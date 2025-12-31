package dev.xerohero.filter.visitor;

import dev.xerohero.filter.operators.*;
import dev.xerohero.filter.operators.comparison.*;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Example visitor that creates a different string representation.
 * Demonstrates how 3rd party apps can process filters.
 */
public class ToStringVisitor implements FilterVisitor<String> {

    @Override
    public String visit(AndFilter filter) {
        return Arrays.stream(filter.filters()).map(f -> f.accept(this)).collect(Collectors.joining(" && ", "(", ")"));
    }

    @Override
    public String visit(OrFilter filter) {
        return Arrays.stream(filter.filters()).map(f -> f.accept(this)).collect(Collectors.joining(" || ", "(", ")"));
    }

    @Override
    public String visit(NotFilter filter) {
        return "!" + filter.filter().accept(this);
    }

    @Override
    public String visit(TrueFilter filter) {
        return "true";
    }

    @Override
    public String visit(FalseFilter filter) {
        return "false";
    }

    @Override
    public String visit(HasPropertyFiltre filter) {
        return "exists(" + filter.getKey() + ")";
    }

    @Override
    public String visit(EqualsFilter filter) {
        return filter.getKey() + " == " + filter.getValue();
    }

    @Override
    public String visit(LessThanFilter filter) {
        return filter.getKey() + " < " + filter.getValue();
    }

    @Override
    public String visit(GreaterThanFilter filter) {
        return filter.getKey() + " > " + filter.getValue();
    }

    @Override
    public String visit(RegexFilter filter) {
        return filter.getKey() + " matches " + filter.getRegex();
    }
}