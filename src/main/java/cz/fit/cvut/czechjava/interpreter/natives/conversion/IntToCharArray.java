package cz.fit.cvut.czechjava.interpreter.natives.conversion;

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
public class IntToCharArray extends Native {

    public IntToCharArray(Heap heap) {
        super(heap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StackValue invoke(StackValue[] args) throws HeapOverflowException, InterpreterException {
        int number = args[0].intValue();
        String s = Integer.toString(number);
        // Create array of chars
        StackValue reference = heap.allocArray(s.length());
        Array charArray = heap.loadArray(reference);

        for (int i = 0; i < s.length(); i++) {
            StackValue charValue = new StackValue(s.charAt(i), StackValue.Type.Primitive);
            charArray.set(i, charValue);
        }

        return reference;
    }
}
