package cz.fit.cvut.czechjava.type;

public class ReferenceType extends Type {

    private String className;

    public ReferenceType(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.className;
    }
}
