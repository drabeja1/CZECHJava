package cz.fit.cvut.czechjava.compiler.model;

import cz.fit.cvut.czechjava.compiler.model.Method;
import cz.fit.cvut.czechjava.interpreter.ClassPool;
import cz.fit.cvut.czechjava.interpreter.exceptions.LookupException;
import cz.fit.cvut.czechjava.type.Type;
import cz.fit.cvut.czechjava.type.Types;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author Jakub
 */
public class Class {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(Class.class.getName());

    protected List<String> flags;
    protected String className;
    protected String superName;
    protected List<Field> fields;
    protected List<Method> methods;
    protected ConstantPool constantPool;

    Class superClass;
    //Including super fields
    Set<Field> allFields;

    public Class() {
        flags = new ArrayList<>();
        fields = new ArrayList<>();
        methods = new ArrayList<>();
    }

    public Class(String className, String superName) {
        this.className = className;
        this.superName = superName;

        flags = new ArrayList<>();
        fields = new ArrayList<>();
        methods = new ArrayList<>();
    }

    public List<Method> getMethods() {
        return methods;
    }

    public void addMethod(Method method) {
        this.methods.add(method);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSuperName() {
        return superName;
    }

    public void setSuperName(String superName) {
        this.superName = superName;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public void addField(Field field) {
        this.fields.add(field);
    }

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    public Type getClassType() {
        return Types.Reference(getClassName());
    }

    public ConstantPool getConstantPool() {
        return constantPool;
    }

    public final void setConstantPool(ConstantPool constantPool) {
        this.constantPool = constantPool;
    }

    public Class getSuperClass() {
        return superClass;
    }

    public void setSuperClass(Class superClass) {
        this.superClass = superClass;
    }

    public Set<Field> getAllFields() {
        if (allFields == null) {
            allFields = new LinkedHashSet<>();
            if (superClass != null) {
                allFields.addAll(getSuperClass().getAllFields());
            }
            allFields.addAll(getFields());
        }

        return allFields;
    }

    public Method lookupMethod(String descriptor, ClassPool classPool) throws LookupException {
        Method methodFromDescriptor = new Method(descriptor);
        int minSimilarity = Integer.MAX_VALUE;
        Method closestMethod = null;
        List<Method> allMethods = new ArrayList<>();
        allMethods.addAll(methods);

        if (superClass != null) {
            try {
                Method superMethod = superClass.lookupMethod(descriptor, classPool);
                allMethods.add(superMethod);
            } catch (LookupException e) {
                // Supress exception in super call - logg it
                //LOGGER.warn(e); // generuje prilis mnoho logu
            }
        }

        for (Method method : allMethods) {
            int similarity = methodFromDescriptor.getSimilarity(method, classPool);
            if (similarity != -1 && similarity < minSimilarity) {
                minSimilarity = similarity;
                closestMethod = method;
            }
        }

        if (closestMethod != null) {
            return closestMethod;
        }

        LOGGER.fatal("Method '" + descriptor + "' not found");
        throw new LookupException("Method '" + descriptor + "' not found");
    }

    public int lookupField(String name) throws LookupException {
        int i = 0;
        String lowerCase = name.toLowerCase();

        for (Field field : getAllFields()) {
            if (field.getName().equals(lowerCase)) {
                return i;
            }
            i++;
        }

        LOGGER.fatal("Field '" + name + "' not found");
        throw new LookupException("Field '" + name + "' not found");
    }

    public Field getField(int position) {
        int i = 0;

        for (Field field : getAllFields()) {
            if (position == i) {
                return field;
            }
            i++;
        }

        return null;
    }

    public boolean inheritsFrom(Class anotherClass) {
        if (this.getClassName().equals(anotherClass.className)) {
            return true;
        }

        if (superClass != null) {
            return superClass.inheritsFrom(anotherClass);
        }

        return false;
    }

    public int getDistanceFrom(Class anotherClass) {
        if (this.getClassName().equals(anotherClass.className)) {
            return 0;
        }

        if (superClass != null) {
            return superClass.getDistanceFrom(anotherClass) + 1;
        }

        LOGGER.fatal("Class  doesn't inherit from " + anotherClass.getClassName() + "'");
        throw new IllegalArgumentException("Class  doesn't inherit from " + anotherClass.getClassName() + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(className).append(">").append(superName);
        return sb.toString();

    }
}
