package cz.fit.cvut.czechjava.interpreter.natives.conversion;

import cz.fit.cvut.czechjava.interpreter.Converter;
import cz.fit.cvut.czechjava.interpreter.InterpreterException;
import cz.fit.cvut.czechjava.interpreter.memory.Array;
import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.memory.HeapOverflow;
import cz.fit.cvut.czechjava.interpreter.natives.Native;
import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public class CharArrayToInt extends Native {

    public CharArrayToInt(Heap heap) {
        super(heap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StackValue invoke(StackValue[] args) throws HeapOverflow, InterpreterException {
        StackValue ref = args[0];

        Array array = heap.loadArray(ref);
        char[] chars = Converter.arrayToCharArray(array);

        int i = Integer.parseInt(new String(chars));

        StackValue result = new StackValue(i, StackValue.Type.Primitive);
        return result;
    }
}
