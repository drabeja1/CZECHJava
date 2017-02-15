package cz.fit.cvut.czechjava.interpreter;

import cz.fit.cvut.czechjava.interpreter.exceptions.LookupException;
import cz.fit.cvut.czechjava.compiler.model.Class;

/**
 *
 * @author Jakub
 */
public class InterpretedClass extends Class {

    /**
     * Adress in class pool
     */
    private int classPoolAddress;

    public InterpretedClass(Class c) {
        super(c.getClassName(), c.getSuperName());

        c.getFields().forEach(field -> this.addField(field));
        c.getMethods().forEach(method -> this.addMethod(new InterpretedMethod(method)));
        setConstantPool(c.getConstantPool());
    }

    public int getClassPoolAddress() {
        return classPoolAddress;
    }

    public void setClassPoolAddress(int classPoolAddress) {
        this.classPoolAddress = classPoolAddress;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public InterpretedMethod lookupMethod(String descriptor, ClassPool classPool) throws LookupException {
        return (InterpretedMethod) super.lookupMethod(descriptor, classPool);
    }

}
