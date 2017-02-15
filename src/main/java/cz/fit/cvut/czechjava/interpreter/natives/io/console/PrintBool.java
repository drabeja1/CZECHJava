package cz.fit.cvut.czechjava.interpreter.natives.io.console;

import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.natives.Native;
import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public class PrintBool extends Native {

    public PrintBool(Heap heap) {
        super(heap);
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code null}
     */
    @Override
    public StackValue invoke(StackValue args[]) {
        boolean value = args[0].boolValue();
        System.out.println(value == false ? "false" : "true");
        return null;
    }
}
