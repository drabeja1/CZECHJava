package cz.fit.cvut.czechjava.interpreter.memory;

import cz.fit.cvut.czechjava.interpreter.memory.garbagecollector.GarbageCollector;
import cz.fit.cvut.czechjava.interpreter.InterpretedClass;
import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public interface Heap {

    public GarbageCollector getGarbageCollector();

    public StackValue allocObject(InterpretedClass objectClass) throws HeapOverflow;

    public StackValue allocArray(int size) throws HeapOverflow;

    public Array loadArray(StackValue reference);

    public Object loadObject(StackValue reference);

    public HeapItem load(StackValue reference);

    public int getSize();

    public void dealloc(StackValue address);

    public StackValue[] getAllocated();

}
