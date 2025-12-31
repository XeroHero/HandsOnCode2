package dev.xerohero.filter.visitor;

import dev.xerohero.filter.Filter;
import dev.xerohero.filter.operators.*;
import dev.xerohero.filter.operators.comparison.*;

/**
 * Visitor interface for type-safe operations on filters.
 * This enables 3rd party applications to inspect and process filters
 * without using instanceof checks.
 */
public interface FilterVisitor<T> {
    T visit(AndFilter filter);
    T visit(OrFilter filter);
    T visit(NotFilter filter);
    T visit(TrueFilter filter);
    T visit(FalseFilter filter);
    T visit(HasPropertyFiltre filter);
    T visit(EqualsFilter filter);
    T visit(LessThanFilter filter);
    T visit(GreaterThanFilter filter);
    T visit(RegexFilter filter);
}