package cz.fit.cvut.czechjava.interpreter.natives.io.console;

import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.natives.Native;
import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public class PrintChar extends Native {

    public PrintChar(Heap heap) {
        super(heap);
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code null}
     */
    @Override
    public StackValue invoke(StackValue[] args) {
        char value = args[0].charValue();
        System.out.println(value);
        return null;
    }
}
