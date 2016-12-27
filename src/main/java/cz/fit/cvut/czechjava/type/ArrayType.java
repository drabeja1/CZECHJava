package cz.fit.cvut.czechjava.type;

public class ArrayType extends Type {

    Type element;

    ArrayType(Type type) {
        element = type;
    }

    public Type getElement() {
        return element;
    }

    @Override
    public String toString() {
        return element + "[]";
    }
}
