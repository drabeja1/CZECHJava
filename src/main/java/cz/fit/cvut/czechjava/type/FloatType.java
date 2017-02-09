package cz.fit.cvut.czechjava.type;

import cz.fit.cvut.czechjava.Globals;

public class FloatType extends Type {

    public static final int SIZE = 4;
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public String toString() {
        return Globals.FLOAT_TYPE_NAME;
    }
}
