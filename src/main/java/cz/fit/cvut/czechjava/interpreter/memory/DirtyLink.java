package cz.fit.cvut.czechjava.interpreter.memory;

import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public class DirtyLink {

    StackValue from;
    StackValue reference;

    public DirtyLink(StackValue from, StackValue reference) {
        this.from = from;
        this.reference = reference;
    }

    public StackValue getFrom() {
        return from;
    }

    public StackValue getReference() {
        return reference;
    }
}
