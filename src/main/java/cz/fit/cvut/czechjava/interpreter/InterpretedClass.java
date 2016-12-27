package cz.fit.cvut.czechjava.interpreter;

import cz.fit.cvut.czechjava.compiler.Class;

/**
 *
 * @author Jakub
 */
public class InterpretedClass extends Class {

    int classPoolAddress;

    public InterpretedClass(Class c) {
        super(c.getClassName(), c.getSuperName());

        //Copy
        c.getFields().forEach(field -> this.addField(field));

        c.getMethods().forEach(method -> this.addMethod(new InterpretedMethod(method)));
        setConstantPool(c.getConstantPool());

        //TODO: Copy flags
    }

    public int getClassPoolAddress() {
        return classPoolAddress;
    }

    public void setClassPoolAddress(int classPoolAddress) {
        this.classPoolAddress = classPoolAddress;
    }

    public InterpretedMethod lookupMethod(String descriptor, ClassPool classPool) throws LookupException {
        return (InterpretedMethod) super.lookupMethod(descriptor, classPool);
    }

}
