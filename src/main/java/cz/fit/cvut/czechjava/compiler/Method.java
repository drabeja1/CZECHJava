package cz.fit.cvut.czechjava.compiler;

import cz.fit.cvut.czechjava.interpreter.ClassPool;
import cz.fit.cvut.czechjava.interpreter.exceptions.LookupException;
import cz.fit.cvut.czechjava.type.ArrayType;
import cz.fit.cvut.czechjava.type.ReferenceType;
import cz.fit.cvut.czechjava.type.Type;
import cz.fit.cvut.czechjava.type.Types;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Jakub
 */
public class Method {

    public enum MethodFlag {
        Native,
        Static;
    }

    protected String name;
    protected List<Type> args;
    protected Type returnType;
    protected ByteCode byteCode;
    protected int localVariablesCount = 0;
    protected String className;
    protected Set<MethodFlag> flags;

    public Method(String name, List<Type> args) {
        this(name, args, null);
    }

    public Method(String name, List<Type> args, String className) {
        this(name, args, className, Types.Void());
    }

    public Method(String name, List<Type> args, String className, Type returnType) {
        this.name = name;
        this.args = args;
        this.returnType = returnType;
        this.className = className;
        this.flags = new HashSet<>();
    }

    public Method(String descriptor) {
        try {

            this.flags = new HashSet<>();

            int classIndex = descriptor.indexOf(".");
            String methodPart = descriptor;
            if (classIndex != -1) {
                this.className = descriptor.substring(0, classIndex);
                methodPart = descriptor.substring(classIndex + 1);
            }

            String[] parts = methodPart.split(":");
            args = new ArrayList<>();
            returnType = Types.Void();

            for (int i = 0; i < parts.length; i++) {
                if (i == 0) {
                    name = parts[i];
                } else {
                    args.add(Types.fromString(parts[i]));
                }
            }
        } catch (java.lang.StackOverflowError e) {
            System.out.println("");
        }
    }

    public int getSimilarity(Method method, ClassPool classPool) throws LookupException {
        if (!method.getName().equals(this.getName())) {
            return -1;
        }

        if (method.getArgs().size() != this.getArgs().size()) {
            return -1;
        }

        int similarity = 0;
        int i = 0;
        for (Type methodArgType : method.getArgs()) {
            Type argType = this.getArgs().get(i);

            // If it's arrays, use their elements
            if (methodArgType instanceof ArrayType && argType instanceof ArrayType) {
                methodArgType = ((ArrayType) methodArgType).getElement();
                argType = ((ArrayType) argType).getElement();
            }

            // If it's both objects
            if (methodArgType instanceof ReferenceType && argType instanceof ReferenceType) {
                String methodArgClassName = ((ReferenceType) methodArgType).getClassName();
                String argClassName = ((ReferenceType) argType).getClassName();

                Class methodArgClass = classPool.lookupClass(methodArgClassName);
                Class argClass = classPool.lookupClass(argClassName);

                // Check whether arguments inherit from each other
                if (argClass.inheritsFrom(methodArgClass)) {
                    // Increase similarity score if it's inheriting from class
                    similarity += argClass.getDistanceFrom(methodArgClass);
                } else {
                    return -1;
                }
            } else if (methodArgType != argType) {
                return -1;
            }
            i++;
        }

        return similarity;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public List<Type> getArgs() {
        return args;
    }

    public void setArgs(List<Type> args) {
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ByteCode getByteCode() {
        return byteCode;
    }

    public void setByteCode(ByteCode byteCode) {
        this.byteCode = byteCode;
    }

    public int getLocalVariablesCount() {
        return localVariablesCount;
    }

    public void setLocalVariablesCount(int localVariablesCount) {
        this.localVariablesCount = localVariablesCount;
    }

    public boolean isStaticMethod() {
        return flags.contains(MethodFlag.Static);
    }

    public boolean isNativeMethod() {
        return flags.contains(MethodFlag.Native);
    }

    public void addFlag(MethodFlag flag) {
        this.flags.add(flag);
    }

    public void addFlags(Set<MethodFlag> flags) {
        this.flags.addAll(flags);
    }

    public Set<MethodFlag> getFlags() {
        return flags;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescriptor() {
        return Method.getDescriptor(name, args, className);
    }

    public static String getDescriptor(String name, List<Type> args, String className) {
        StringBuilder sb = new StringBuilder();
        if (className != null) {
            sb.append(className).append(".");
        }
        sb.append(name);

        args.forEach(arg -> sb.append(":").append(arg.toString().toLowerCase()));

        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getDescriptor();
    }
}
