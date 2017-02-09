package cz.fit.cvut.czechjava.interpreter.natives.io.file;

import cz.fit.cvut.czechjava.interpreter.Converter;
import cz.fit.cvut.czechjava.interpreter.InterpreterException;
import cz.fit.cvut.czechjava.interpreter.memory.Array;
import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.natives.Native;
import cz.fit.cvut.czechjava.interpreter.StackValue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 *
 * @author Jakub
 */
public class OpenReader extends Native {

    public OpenReader(Heap heap) {
        super(heap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StackValue invoke(StackValue[] args) throws InterpreterException {
        StackValue charRef = args[0];

        Array array = heap.loadArray(charRef);
        char[] chars = Converter.arrayToCharArray(array);

        String fileName = new String(chars);
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            int handle = Readers.getInstance().add(br);
            return new StackValue(handle, StackValue.Type.Primitive);
        } catch (FileNotFoundException e) {
            throw new InterpreterException(e.getMessage());
        }
    }
}
