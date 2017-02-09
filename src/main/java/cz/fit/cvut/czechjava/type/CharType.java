package cz.fit.cvut.czechjava.type;

import cz.fit.cvut.czechjava.Globals;

public class CharType extends Type {

    public static int size = 4;

    @Override
    public String toString() {
        return Globals.CHAR_TYPE_NAME;
    }
}
