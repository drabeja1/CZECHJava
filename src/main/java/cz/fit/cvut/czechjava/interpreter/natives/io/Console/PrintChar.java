package cz.fit.cvut.czechjava.interpreter.natives.io.Console;

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
     * 
     * @param args char
     * @return 
     */
    @Override
    public StackValue invoke(StackValue[] args) {
        StackValue arg = args[0];
        char value = arg.charValue();
        System.out.println(value);
        return null;
    }
}
