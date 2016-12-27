package cz.fit.cvut.czechjava.interpreter.natives.io.File;

import cz.fit.cvut.czechjava.interpreter.InterpreterException;
import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.memory.HeapOverflow;
import cz.fit.cvut.czechjava.interpreter.memory.Array;
import cz.fit.cvut.czechjava.interpreter.natives.Native;
import cz.fit.cvut.czechjava.interpreter.StackValue;

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author Jakub
 */
public class ReadLine extends Native {

    public ReadLine(Heap heap) {
        super(heap);
    }

    @Override
    public StackValue invoke(StackValue[] args) throws HeapOverflow, InterpreterException {
        int handle = args[0].intValue();

        BufferedReader br = Readers.getInstance().get(handle);

        try {
            StackValue reference;
            String line = br.readLine();

            if (line != null) {

                //Create array of chars
                reference = heap.allocArray(line.length());
                Array charArray = heap.loadArray(reference);

                for (int i = 0; i < line.length(); i++) {
                    StackValue charValue = new StackValue(line.charAt(i), StackValue.Type.Primitive);
                    charArray.set(i, charValue);
                }

            } else {
                //Null reference
                reference = new StackValue(0, StackValue.Type.Pointer);
            }

            return reference;

        } catch (IOException e) {
            throw new InterpreterException(e.getMessage());
        }

    }
}
