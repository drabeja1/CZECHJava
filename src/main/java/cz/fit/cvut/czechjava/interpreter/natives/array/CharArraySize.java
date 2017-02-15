package cz.fit.cvut.czechjava.interpreter.natives.array;

import cz.fit.cvut.czechjava.interpreter.exceptions.InterpreterException;
import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.exceptions.HeapOverflowException;
import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public class CharArraySize extends ArraySize {

    public CharArraySize(Heap heap) {
        super(heap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StackValue invoke(StackValue[] args) throws HeapOverflowException, InterpreterException {
        return super.invoke(args);
    }
}
