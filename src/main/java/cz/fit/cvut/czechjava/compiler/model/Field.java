package cz.fit.cvut.czechjava.compiler.model;

import cz.fit.cvut.czechjava.type.Type;
import cz.fit.cvut.czechjava.type.Types;

import java.util.List;

/**
 *
 * @author Jakub
 */
public class Field {

    protected List<String> flags;
    protected String name;
    protected Type type;

    public Field(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public Field(String descriptor) {
        String[] parts = descriptor.split(":");
        this.name = parts[0];
        this.type = Types.fromString(parts[1]);
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

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getName() + ":" + getType();
    }
}
