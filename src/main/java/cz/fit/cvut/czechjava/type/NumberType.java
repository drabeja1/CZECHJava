package cz.fit.cvut.czechjava.type;

public class NumberType extends Type {

    public final static int size = 4;
    static java.lang.String name = "cislo";

    @Override
    public String toString() {
        return name;
    }
}
