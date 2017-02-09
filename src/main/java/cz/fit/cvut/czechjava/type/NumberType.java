package cz.fit.cvut.czechjava.type;

import cz.fit.cvut.czechjava.Globals;

public class NumberType extends Type {

    public final static int SIZE = 4;

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Globals.NUMBER_TYPE_NAME;
    }
}
