package cz.fit.cvut.czechjava.type;

public class ArrayType extends Type {

    private final Type element;

    ArrayType(Type type) {
        element = type;
    }

    public Type getElement() {
        return element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return element + "[]";
    }
}
