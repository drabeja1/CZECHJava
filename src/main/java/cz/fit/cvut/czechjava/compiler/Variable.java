package cz.fit.cvut.czechjava.compiler;

import cz.fit.cvut.czechjava.type.Type;

/**
 *
 * @author Jakub
 */
public class Variable {

    protected String name;
    protected Type type;
    protected boolean initialized = false;

    public Variable(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
