package cz.fit.cvut.czechjava.interpreter.natives.io.Console;

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
     *
     * @param args int
     * @return
     */
    @Override
    public StackValue invoke(StackValue args[]) {
        StackValue arg = args[0];
        int value = arg.intValue();

        System.out.println(value);
        return null;
    }
}
