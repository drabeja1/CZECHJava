package cz.fit.cvut.czechjava.type;

import java.util.HashMap;
import java.util.Map;

public class Types {

    static Map<String, Type> singletons;

    private static void addSingleton(Type type) {
        singletons.put(type.toString(), type);
    }

    public static void init() {
        singletons = new HashMap<>();

        addSingleton(new BooleanType());
        addSingleton(new CharType());
        addSingleton(new FloatType());
        addSingleton(new NumberType());
        addSingleton(new StringType());
        addSingleton(new VoidType());
        addSingleton(new ArrayType(Boolean()));
        addSingleton(new ArrayType(Number()));
        addSingleton(new ArrayType(Char()));
        addSingleton(new ArrayType(Float()));
    }

    public static Type getSingleton(String name) {
        if (singletons == null) {
            init();
        }
        return singletons.get(name);
    }

    public static BooleanType Boolean() {
        return (BooleanType) getSingleton(BooleanType.name);
    }

    public static NumberType Number() {
        return (NumberType) getSingleton(NumberType.name);
    }

    public static CharType Char() {
        return (CharType) getSingleton(CharType.name);
    }

    public static FloatType Float() {
        return (FloatType) getSingleton(FloatType.name);
    }

    public static StringType String() {
        return (StringType) getSingleton(StringType.name);
    }

    public static VoidType Void() {
        return (VoidType) getSingleton(VoidType.name);
    }

    public static ArrayType NumberArray() {
        return Array(Number());
    }

    public static ArrayType FloatArray() {
        return Array(Float());
    }

    public static ArrayType CharArray() {
        return Array(Char());
    }

    public static ArrayType BooleanArray() {
        return Array(Boolean());
    }

    public static ArrayType Array(Type type) {
        ArrayType arrayType = (ArrayType) getSingleton(type.toString() + "[]");

        if (arrayType == null) {
            arrayType = new ArrayType(type);
            addSingleton(arrayType);
        }
        return arrayType;
    }

    public static Type Reference(String className) {
        className = className.toLowerCase();
        Type type = getSingleton(className);
        if (type == null) {
            type = new ReferenceType(className);
            addSingleton(type);
        }
        return type;
    }

    public static Type fromString(String name) {
        Type type = getSingleton(name);
        if (type == null) {
            if (name.length() > 2 && name.substring(name.length() - 2, name.length()).equals("[]")) {
                type = Array(Reference(name.substring(0, name.length() - 2)));
            } else {
                type = Reference(name);
            }
            addSingleton(type);
        }

        return type;
    }
}