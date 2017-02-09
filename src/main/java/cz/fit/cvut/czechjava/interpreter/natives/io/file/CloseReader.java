package cz.fit.cvut.czechjava.interpreter.natives.io.file;

import cz.fit.cvut.czechjava.interpreter.InterpreterException;
import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.memory.HeapOverflow;
import cz.fit.cvut.czechjava.interpreter.natives.Native;
import cz.fit.cvut.czechjava.interpreter.StackValue;

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author Jakub
 */
public class CloseReader extends Native {

    public CloseReader(Heap heap) {
        super(heap);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StackValue invoke(StackValue[] args) throws HeapOverflow, InterpreterException {
        int handle = args[0].intValue();

        BufferedReader br = Readers.getInstance().get(handle);
        try {
            br.close();
            Readers.getInstance().remove(handle);
        } catch (IOException e) {
            throw new InterpreterException(e.getMessage());
        }
        return null;
    }
}
