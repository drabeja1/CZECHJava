package cz.fit.cvut.czechjava.interpreter;

import cz.fit.cvut.czechjava.interpreter.exceptions.InterpreterException;
import cz.fit.cvut.czechjava.compiler.model.Method;
import cz.fit.cvut.czechjava.type.NumberType;

/**
 *
 * @author Jakub
 */
public final class Frame extends ByteArrayWrapper {

    final int RETURN_ADDRESS_SIZE = NumberType.SIZE;
    final int LOCAL_VAR_BYTE_SIZE = 4;

    protected int maxSize;
    protected int count;
    protected int localVariablesCount = 0;

    protected String methodName;

    public Frame(int size, int returnAddress, StackValue thisReference, Method method) throws InterpreterException {
        byteArray = new byte[size];
        maxSize = size;
        count = 0;

        // Save return address
        setBytes(0, TypeConverter.intToByteArray(returnAddress));
        // Push this as a first variable
        set(RETURN_ADDRESS_SIZE, thisReference);
        // Arguments and this ref counted in locals
        localVariablesCount = method.getLocalVariablesCount();
        // For stack trace
        methodName = method.getName();

        count = getStackOffset();
    }

    public void push(StackValue i) {
        overflowCheck(StackValue.SIZE);
        set(count, i);
        count += StackValue.SIZE;
    }

    public StackValue pop() {
        underflowCheck(StackValue.SIZE);
        StackValue value = get(count - StackValue.SIZE);
        count -= StackValue.SIZE;
        return value;
    }

    public void set(int from, StackValue value) {
        byte[] bytes = value.getBytes();
        setBytes(from, bytes);
    }

    public StackValue get(int from) {
        return new StackValue(getBytes(from));
    }

    public void storeVariable(int index, StackValue value) {
        if (index > localVariablesCount - 1) {
            throw new IndexOutOfBoundsException("Trying to store to non-existent variable");
        }
        set(getVariablePosition(index), value);
    }

    public StackValue loadVariable(int index) {
        if (index > localVariablesCount - 1) {
            throw new IndexOutOfBoundsException("Trying to load non-existent variable");
        }
        return get(getVariablePosition(index));
    }

    protected int getVariablePosition(int index) {
        return RETURN_ADDRESS_SIZE + index * LOCAL_VAR_BYTE_SIZE;
    }

    protected int getReturnAddress() {
        return TypeConverter.byteArrayToInt(getBytes(0));
    }

    public int getStackOffset() {
        return RETURN_ADDRESS_SIZE + localVariablesCount * LOCAL_VAR_BYTE_SIZE;
    }

    public int getSize() {
        return count;
    }

    public int getLocalVariablesCount() {
        return localVariablesCount;
    }

    public String getMethodName() {
        return methodName;
    }

    private void underflowCheck(int size) {
        if (count - size < getStackOffset()) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void overflowCheck(int size) {
        if (count + size >= maxSize) {
            throw new StackOverflowError();
        }
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getReturnAddress()).append("\n").append("-----------\n");

        // Show local variables
        for (int i = 0; i < localVariablesCount; i++) {
            int start = getVariablePosition(i);
            StackValue var = get(start);
            sb.append("Var ").append(i).append(": ").append(var).append("\n");
        }
        sb.append("-----------\n");
        // Show values on stack
        for (int j = getStackOffset(); j < count; j++) {
            sb.append(byteArray[j]).append(" ");
        }

        return sb.toString();
    }
}
