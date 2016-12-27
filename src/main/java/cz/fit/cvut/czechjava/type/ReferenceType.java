package cz.fit.cvut.czechjava.type;

public class ReferenceType extends Type {

    private String className;

    public ReferenceType(String className) {
        this.className = className;
    }

    public java.lang.String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return this.className;
    }
}
