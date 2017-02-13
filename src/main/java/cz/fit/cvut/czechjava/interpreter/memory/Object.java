package cz.fit.cvut.czechjava.interpreter.memory;

import cz.fit.cvut.czechjava.compiler.model.Field;
import cz.fit.cvut.czechjava.interpreter.ClassPool;
import cz.fit.cvut.czechjava.interpreter.Converter;
import cz.fit.cvut.czechjava.interpreter.InterpretedClass;
import cz.fit.cvut.czechjava.interpreter.exceptions.InterpreterException;
import cz.fit.cvut.czechjava.interpreter.memory.garbagecollector.State;
import cz.fit.cvut.czechjava.interpreter.StackValue;

import java.util.Set;

/**
 *
 * @author Jakub
 */
public class Object extends HeapItem {

    final int OBJECT_FIELD_SIZE = 4;
    final int OBJECT_CLASS_ADDRESS_SIZE = 4;
    final int OBJECT_SIZE_TYPE = 4;

    final int OBJECT_HEADER_SIZE = GC_STATE_SIZE + OBJECT_CLASS_ADDRESS_SIZE + OBJECT_SIZE_TYPE;

    public Object(InterpretedClass objectClass) {

        Set<Field> fieldList = objectClass.getAllFields();
        int numberOfFields = fieldList.size();

        int size = OBJECT_HEADER_SIZE + OBJECT_FIELD_SIZE * numberOfFields;

        byteArray = new byte[size];

        //Set class address
        setBytes(GC_STATE_SIZE, Converter.intToByteArray(objectClass.getClassPoolAddress()));

        //Set size
        setBytes(GC_STATE_SIZE + OBJECT_CLASS_ADDRESS_SIZE, Converter.intToByteArray(size));

        this.setGCState(State.Dead);
    }

    protected int getClassAddress() {
        return Converter.byteArrayToInt(getBytes(GC_STATE_SIZE));
    }

    protected int getSize() {
        return Converter.byteArrayToInt(getBytes(GC_STATE_SIZE + OBJECT_CLASS_ADDRESS_SIZE));
    }

    protected int getFieldsSize() {
        return getSize() - OBJECT_HEADER_SIZE;
    }

    public int getFieldsNumber() {
        return getFieldsSize() / OBJECT_FIELD_SIZE;
    }

    public StackValue getField(int index) {
        return new StackValue(getBytes(OBJECT_HEADER_SIZE + index * OBJECT_FIELD_SIZE));
    }

    public void setField(int index, StackValue value) {
        setBytes(OBJECT_HEADER_SIZE + index * OBJECT_FIELD_SIZE, value.getBytes());
    }

    public InterpretedClass loadClass(ClassPool pool) throws InterpreterException {
        return pool.getClass(getClassAddress());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(super.toString())
                .append("\n")
                .append("Class: ")
                .append(getClassAddress())
                .append("\n")
                .append("Size: ")
                .append(getSize())
                .append("\n");

        for (int i = 0; i < getFieldsNumber(); i++) {
            sb.append(getField(i)).append(" ");
        }

        return sb.toString();
    }
}
