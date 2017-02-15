package cz.fit.cvut.czechjava.interpreter.natives.io.console;

import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.natives.Native;
import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public class PrintFloat extends Native {

    public PrintFloat(Heap heap) {
        super(heap);
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code null}
     */
    @Override
    public StackValue invoke(StackValue args[]) {
        float value = args[0].floatValue();
        System.out.println(value);
        return null;
    }
}
