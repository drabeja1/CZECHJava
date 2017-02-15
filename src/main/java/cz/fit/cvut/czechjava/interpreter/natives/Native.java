package cz.fit.cvut.czechjava.interpreter.natives;

import cz.fit.cvut.czechjava.interpreter.exceptions.InterpreterException;
import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.exceptions.HeapOverflowException;
import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public abstract class Native {

    protected Heap heap;

    public Native(Heap heap) {
        this.heap = heap;
    }

    public abstract StackValue invoke(StackValue args[]) throws HeapOverflowException, InterpreterException;
}
