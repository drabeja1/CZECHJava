package cz.fit.cvut.czechjava.interpreter.natives;

import cz.fit.cvut.czechjava.compiler.Method;
import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.memory.HeapOverflow;
import cz.fit.cvut.czechjava.interpreter.natives.array.CharArraySize;
import cz.fit.cvut.czechjava.interpreter.natives.array.IntArraySize;
import cz.fit.cvut.czechjava.interpreter.natives.array.ReferenceArraySize;
import cz.fit.cvut.czechjava.interpreter.natives.conversion.CharArrayToInt;
import cz.fit.cvut.czechjava.interpreter.natives.conversion.IntToCharArray;
import cz.fit.cvut.czechjava.interpreter.natives.io.File.CloseReader;
import cz.fit.cvut.czechjava.interpreter.natives.io.File.OpenReader;
import cz.fit.cvut.czechjava.interpreter.natives.io.File.ReadLine;
import cz.fit.cvut.czechjava.interpreter.natives.math.LogInt;
import cz.fit.cvut.czechjava.interpreter.natives.math.PowInt;
import cz.fit.cvut.czechjava.type.Type;
import cz.fit.cvut.czechjava.type.Types;
import cz.fit.cvut.czechjava.compiler.Class;
import cz.fit.cvut.czechjava.interpreter.ClassPool;
import cz.fit.cvut.czechjava.interpreter.InterpreterException;
import cz.fit.cvut.czechjava.interpreter.LookupException;
import cz.fit.cvut.czechjava.interpreter.natives.io.Console.PrintBool;
import cz.fit.cvut.czechjava.interpreter.natives.io.Console.PrintChar;
import cz.fit.cvut.czechjava.interpreter.natives.io.Console.PrintChars;
import cz.fit.cvut.czechjava.interpreter.natives.io.Console.PrintFloat;
import cz.fit.cvut.czechjava.interpreter.natives.io.Console.PrintInt;
import cz.fit.cvut.czechjava.interpreter.StackValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Jakub
 */
public class Natives {

    protected Map<String, Native> nativesMap;
    protected Class nativeClass;
    protected ClassPool classPool;
    protected Heap heap;

    public Natives(Heap heap, ClassPool classPool) {
        this.heap = heap;
        this.classPool = classPool;
        init();
    }

    private List<Type> argsList(Type... types) {
        return new ArrayList<>(Arrays.asList(types));
    }

    private void addNative(String descriptor, Native implementation) {
        nativeClass.addMethod(new Method(descriptor));
        nativesMap.put(descriptor, implementation);
    }

    private void init() {
        if (nativesMap == null) {
            nativeClass = new Class("Natives", null);
            nativesMap = new HashMap<>();
            addNative("print:" + Types.Char(), new PrintChar(heap));
            addNative("print:" + Types.CharArray(), new PrintChars(heap));
            addNative("print:" + Types.Number(), new PrintInt(heap));
            addNative("print:" + Types.Boolean(), new PrintBool(heap));
            addNative("print:" + Types.Float(), new PrintFloat(heap));
            addNative("logint:" + Types.Number() + ":" + Types.Number(), new LogInt(heap));
            addNative("powint:" + Types.Number() + ":" + Types.Number(), new PowInt(heap));
            addNative("openreader:" + Types.CharArray(), new OpenReader(heap));
            addNative("readline:" + Types.Number(), new ReadLine(heap));
            addNative("closereader:" + Types.Number(), new CloseReader(heap));
            addNative("arraysize:" + Types.CharArray(), new CharArraySize(heap));
            addNative("arraysize:" + Types.NumberArray(), new IntArraySize(heap));
            addNative("arraysize:" + "Perpetummobile[]", new ReferenceArraySize(heap));
            addNative("chararraytoint:" + Types.CharArray(), new CharArrayToInt(heap));
            addNative("inttochararray:" + Types.Number(), new IntToCharArray(heap));
        }
    }

    public boolean nativeExist(String descriptor) {
        try {
            nativeClass.lookupMethod(descriptor, classPool);
            return true;
        } catch (LookupException e) {
            return false;
        }
    }

    public StackValue invoke(String descriptor, StackValue[] args) throws InterpreterException, HeapOverflow {
        try {
            Method method = nativeClass.lookupMethod(descriptor, classPool);
            return nativesMap.get(method.getDescriptor()).invoke(args);
        } catch (LookupException ex) {
            throw new InterpreterException("Trying to call non-existent method '" + descriptor + "'");
        }
    }

}
