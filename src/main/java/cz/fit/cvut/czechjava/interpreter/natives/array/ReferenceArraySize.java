package cz.fit.cvut.czechjava.interpreter.natives.array;

import cz.fit.cvut.czechjava.interpreter.InterpreterException;
import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.memory.HeapOverflow;
import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public class ReferenceArraySize extends ArraySize {

    public ReferenceArraySize(Heap heap) {
        super(heap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StackValue invoke(StackValue[] args) throws HeapOverflow, InterpreterException {
        return super.invoke(args);
    }
}
