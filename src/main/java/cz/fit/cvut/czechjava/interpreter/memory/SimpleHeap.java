package cz.fit.cvut.czechjava.interpreter.memory;

import cz.fit.cvut.czechjava.interpreter.exceptions.HeapOverflowException;
import cz.fit.cvut.czechjava.interpreter.InterpretedClass;
import cz.fit.cvut.czechjava.interpreter.memory.garbagecollector.GarbageCollector;
import cz.fit.cvut.czechjava.interpreter.StackValue;

import java.util.LinkedList;

/**
 *
 * @author Jakub
 */
public class SimpleHeap implements Heap {

    protected HeapItem[] objectArray;
    protected LinkedList<Integer> emptyList;
    protected GarbageCollector garbageCollector;
    
    private int size;
    private int addressStart;

    public SimpleHeap(int size, int addressStart) {
        objectArray = new HeapItem[size];
        emptyList = new LinkedList<>();
        this.addressStart = addressStart;
        this.size = size;

        for (int i = size - 1; i >= 0; i--) {
            emptyList.add(i);
        }
    }

    public SimpleHeap(int size) {
        // We start address at, 0 is reserved for null
        this(size, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GarbageCollector getGarbageCollector() {
        return garbageCollector;
    }

    public void setGarbageCollector(GarbageCollector garbageCollector) {
        this.garbageCollector = garbageCollector;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StackValue allocObject(InterpretedClass objectClass) throws HeapOverflowException {
        cz.fit.cvut.czechjava.interpreter.memory.Object object = new Object(objectClass);
        return alloc(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StackValue allocArray(int size) throws HeapOverflowException {
        Array array = new Array(size);
        return alloc(array);
    }

    public StackValue alloc(HeapItem object) throws HeapOverflowException {
        if (isOutOfSpace()) {
            throw new HeapOverflowException();
        }

        int index = emptyList.getLast();
        emptyList.removeLast();
        objectArray[index] = object;

        return indexToReference(index);
    }

    public boolean isOutOfSpace() {
        return (spaceLeft() == 0);
    }

    public int spaceLeft() {
        return emptyList.size();
    }

    public int spaceUsed() {
        return size - spaceLeft();
    }

    public int referenceToIndex(StackValue reference) {
        return reference.intValue() - addressStart;
    }

    // 0 reference is null
    // sReturns pointer
    public StackValue indexToReference(int index) {
        return new StackValue(index + addressStart, StackValue.Type.Pointer);
    }

    public boolean referenceIsOutOfBounds(StackValue reference) {
        int i = referenceToIndex(reference);
        return (i < 0 || i >= getSize());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Array loadArray(StackValue reference) {
        HeapItem obj = load(reference);

        if (obj == null) {
            throw new NullPointerException();
        }

        if (obj instanceof Array) {
            return (Array) obj;
        } else {
            throw new IllegalArgumentException("Type mismatch on " + reference + ", expected ArrayType");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object loadObject(StackValue reference) {
        HeapItem obj = load(reference);
        if (obj == null) {
            throw new NullPointerException();
        }

        if (obj instanceof Object) {
            return (Object) obj;
        } else {
            throw new IllegalArgumentException("Type mismatch on " + reference + ", expected Object");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HeapItem load(StackValue reference) {
        if (reference.isNullPointer()) {
            throw new NullPointerException();
        }

        int index = referenceToIndex(reference);
        if (index > size - 1 || index < 0) {
            throw new IndexOutOfBoundsException("Can't load object on address: " + reference);
        }

        return objectArray[index];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dealloc(StackValue address) {
        int index = referenceToIndex(address);
        objectArray[index] = null;
        emptyList.addFirst(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StackValue[] getAllocated() {
        StackValue[] allocated = new StackValue[spaceUsed()];

        int pos = 0;
        for (int i = 0; i < getSize(); i++) {
            StackValue ref = indexToReference(i);
            HeapItem item = load(ref);
            if (item != null) {
                allocated[pos] = ref;
                pos++;
            }
        }

        return allocated;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < getSize(); i++) {
            StackValue ref = indexToReference(i);
            if (load(ref) != null) {
                sb.append(ref).append(" ");
            }
        }

        return sb.toString();
    }
}
