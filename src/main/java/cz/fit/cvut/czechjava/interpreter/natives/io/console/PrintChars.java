package cz.fit.cvut.czechjava.interpreter.natives.io.console;

import cz.fit.cvut.czechjava.interpreter.Converter;
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
     *
     * @param args char[]
     * @return
     */
    @Override
    public StackValue invoke(StackValue args[]) {
        StackValue ref = args[0];

        Array array = heap.loadArray(ref);
        char[] chars = Converter.arrayToCharArray(array);

        System.out.println(chars);
        return null;
    }
}
