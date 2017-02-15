package cz.fit.cvut.czechjava.interpreter.natives.io.console;

import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.natives.Native;
import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public class PrintInt extends Native {

    public PrintInt(Heap heap) {
        super(heap);
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code null}
     */
    @Override
    public StackValue invoke(StackValue args[]) {
        int value = args[0].intValue();
        System.out.println(value);
        return null;
    }
}
