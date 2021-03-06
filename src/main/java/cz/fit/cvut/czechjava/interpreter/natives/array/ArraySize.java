package cz.fit.cvut.czechjava.interpreter.natives.array;

import cz.fit.cvut.czechjava.interpreter.exceptions.InterpreterException;
import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.exceptions.HeapOverflowException;
import cz.fit.cvut.czechjava.interpreter.natives.Native;
import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public abstract class ArraySize extends Native {

    public ArraySize(Heap heap) {
        super(heap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StackValue invoke(StackValue[] args) throws HeapOverflowException, InterpreterException {
        StackValue ref = args[0];
        int size = heap.loadArray(ref).getSize();
        return new StackValue(size, StackValue.Type.Primitive);
    }
}
