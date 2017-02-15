package cz.fit.cvut.czechjava.interpreter.natives.io.console;

import cz.fit.cvut.czechjava.interpreter.TypeConverter;
import cz.fit.cvut.czechjava.interpreter.memory.Array;
import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.natives.Native;
import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public class PrintChars extends Native {

    public PrintChars(Heap heap) {
        super(heap);
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code null}
     */
    @Override
    public StackValue invoke(StackValue args[]) {
        Array array = heap.loadArray(args[0]);
        System.out.println(TypeConverter.arrayToCharArray(array));
        return null;
    }
}
