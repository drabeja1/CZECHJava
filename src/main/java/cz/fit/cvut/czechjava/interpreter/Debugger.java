package cz.fit.cvut.czechjava.interpreter;

import cz.fit.cvut.czechjava.interpreter.exceptions.LookupException;
import cz.fit.cvut.czechjava.interpreter.exceptions.InterpreterException;
import cz.fit.cvut.czechjava.compiler.ConstantPool;
import cz.fit.cvut.czechjava.compiler.Field;
import cz.fit.cvut.czechjava.interpreter.memory.Array;
import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.memory.HeapItem;
import cz.fit.cvut.czechjava.interpreter.memory.Object;
import cz.fit.cvut.czechjava.type.Types;
import org.apache.log4j.Logger;

/**
 *
 * @author Jakub
 */
public class Debugger {
    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(Debugger.class.getName());

    private static Heap heap;
    private static ClassPool classPool;
    private static ConstantPool constantPool;
    private final static int INDENTATION = 2;

    public static void init(Heap heap, ClassPool classPool, ConstantPool constantPool) {
        Debugger.heap = heap;
        Debugger.classPool = classPool;
        Debugger.constantPool = constantPool;
    }

    public static void print(StackValue reference) {
        print(reference, 0);
    }

    public static void print(StackValue reference, int indent) {

        System.out.print(reference);

        if (reference.isPointer() && !reference.isNullPointer()) {
            HeapItem item = heap.load(reference);
            print(item, indent);
        }
    }

    public static void print(HeapItem heapObj) {
        print(heapObj, 0);
    }

    protected static void printIndentation(int size) {
        String indentSpace = new String(new char[size]).replace('\0', ' ');
        System.out.print(indentSpace);
    }

    public static void print(Heap heap) {
        for (StackValue ref : heap.getAllocated()) {
            print(ref);
        }
    }

    public static void printString(Object obj) {

        //TODO: call naHodnePismenek somehow?
        try {
            InterpretedClass clazz = obj.loadClass(classPool);
            int fieldPos = clazz.lookupField("chachari");
            StackValue ref = obj.getField(fieldPos);
            char[] str = Converter.arrayToCharArray(heap.loadArray(ref));
            System.out.println("{" + clazz.getClassName() + "} = '" + new String(str) + "'");

        } catch (LookupException | InterpreterException e) {
            LOGGER.equals(e);
        }
    }

    public static void print(HeapItem heapObj, int indent) {

        indent += INDENTATION;

        if (heapObj instanceof Object) {
            Object obj = (Object) heapObj;
            try {
                InterpretedClass clazz = obj.loadClass(classPool);
                if (clazz.getClassType() == Types.String()) {
                    printString(obj);
                } else {
                    System.out.println("{" + clazz.getClassName() + "}");
                    int i = 0;

                    for (Field field : clazz.getAllFields()) {
                        StackValue fieldRef = obj.getField(i);
                        printIndentation(indent);
                        System.out.print(field.getName() + " = ");
                        print(fieldRef, indent);

                        i++;
                    }
                }

            } catch (InterpreterException e) {
                System.out.println(e.getMessage());
            }
        } else if (heapObj instanceof Array) {
            Array array = (Array) heapObj;
            System.out.println("{Array}");
            for (int i = 0; i < array.getSize(); i++) {
                StackValue itemRef = array.get(i);
                if (!itemRef.isNullPointer()) {
                    printIndentation(indent);
                    System.out.print(i + " = ");
                    print(itemRef, indent);
                }
            }
        }
    }
}
