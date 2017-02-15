package cz.fit.cvut.czechjava.interpreter;

import cz.fit.cvut.czechjava.interpreter.exceptions.LookupException;
import cz.fit.cvut.czechjava.compiler.model.Class;
import cz.fit.cvut.czechjava.utilities.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jakub
 */
public class ClassPool {

    private final List<InterpretedClass> classes;

    public ClassPool(List<Class> classes) throws LookupException {
        this.classes = new ArrayList<>();

        // Convert compiled classes to interpreted classes
        classes.forEach(c -> addClass(new InterpretedClass(c)));

        // Find super class
        for (InterpretedClass ic : this.classes) {
            if (!StringUtils.isNullOrEmpty(ic.getSuperName())) {
                ic.setSuperClass(lookupClass(ic.getSuperName()));
            }
        }
    }

    public final InterpretedClass lookupClass(String name) throws LookupException {
        String lowercase = name.toLowerCase();
        for (InterpretedClass c : classes) {
            if (c.getClassName().equals(lowercase)) {
                return c;
            }
        }
        throw new LookupException("Class '" + name + "' not found");
    }

    public InterpretedClass getClass(int address) {
        if (0 > address || classes.size() < address) {
            throw new IllegalArgumentException("Couldn't find class with address " + address);
        }
        return classes.get(address);
    }

    public int addClass(InterpretedClass c) {
        classes.add(c);

        int address = classes.indexOf(c);
        c.setClassPoolAddress(address);

        return address;
    }

    public void removeClass(InterpretedClass c) {
        classes.remove(c);
    }

    public List<InterpretedClass> getClasses() {
        return classes;
    }

}
