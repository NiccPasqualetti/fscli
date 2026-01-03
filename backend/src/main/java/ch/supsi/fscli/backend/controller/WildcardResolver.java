package ch.supsi.fscli.backend.controller;

import ch.supsi.fscli.backend.exceptions.FileSystemException;

import java.util.List;

public interface WildcardResolver {

    /**
     * Expand a single operand that may contain '*'.
     * Return a list of zero, one, or many concrete paths.
     */
    List<String> expandOperand(String operand) throws FileSystemException;

    /**
     * Convenience method to expand a list of operands.
     */
    default List<String> expandOperands(Iterable<String> operands) throws FileSystemException {
        List<String> result = new java.util.ArrayList<>();
        for (String op : operands) {
            result.addAll(expandOperand(op));
        }
        return result;
    }
}
