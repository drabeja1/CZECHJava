package cz.fit.cvut.czechjava.interpreter;

import cz.fit.cvut.czechjava.compiler.Class;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jakub
 */
public class ClassPool {

    List<InterpretedClass> classes;

    public ClassPool(List<Class> classes) throws LookupException {
        this.classes = new ArrayList<>();

        //Convert compiled classes to interpreted classes
        classes.forEach(c -> addClass(new InterpretedClass(c)));

        //Find super class
        for (InterpretedClass ic : this.classes) {
            if (ic.getSuperName() != null && ic.getSuperName().length() > 0) {
                ic.setSuperClass(lookupClass(ic.getSuperName()));
            }
        }
    }

    public InterpretedClass lookupClass(String name) throws LookupException {
        String lowercase = name.toLowerCase();

        for (InterpretedClass c : classes) {
            if (c.getClassName().equals(lowercase)) {
                return c;
            }
        }

        throw new LookupException("Class '" + name + "' not found");
    }

    public InterpretedClass getClass(int address) throws InterpreterException {
        if (classes.size() < address) {
            throw new InterpreterException("Couldn't find class with address " + address);
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
