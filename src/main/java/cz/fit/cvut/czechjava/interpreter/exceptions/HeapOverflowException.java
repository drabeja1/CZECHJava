package cz.fit.cvut.czechjava.interpreter.exceptions;

/**
 *
 * @author Jakub
 */
public class HeapOverflowException extends Exception {
    
    public HeapOverflowException() {
        super("Heap overflow!");
    }

    private static final long serialVersionUID = 538598705315511528L;
}
