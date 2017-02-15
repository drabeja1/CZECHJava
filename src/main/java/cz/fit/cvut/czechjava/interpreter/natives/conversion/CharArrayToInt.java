package cz.fit.cvut.czechjava.interpreter.natives.conversion;

import cz.fit.cvut.czechjava.interpreter.TypeConverter;
import cz.fit.cvut.czechjava.interpreter.exceptions.InterpreterException;
import cz.fit.cvut.czechjava.interpreter.memory.Array;
import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.exceptions.HeapOverflowException;
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
    public StackValue invoke(StackValue[] args) throws HeapOverflowException, InterpreterException {
        Array array = heap.loadArray(args[0]);
        char[] chars = TypeConverter.arrayToCharArray(array);
        int i = Integer.parseInt(new String(chars));
        return new StackValue(i, StackValue.Type.Primitive);
    }
}
