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
     *
     * @param args float
     * @return
     */
    @Override
    public StackValue invoke(StackValue args[]) {
        StackValue arg = args[0];
        float value = arg.floatValue();
        System.out.println(value);
        return null;
    }
}
