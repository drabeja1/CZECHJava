package cz.fit.cvut.czechjava.interpreter.memory;

import cz.fit.cvut.czechjava.interpreter.memory.garbagecollector.State;
import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public class Array extends HeapItem {

    final int ITEM_SIZE = StackValue.size;
    final int HEADER_SIZE = GC_STATE_SIZE;

    int capacity;

    public Array(int size) {

        this.capacity = size;
        byteArray = new byte[HEADER_SIZE + size * ITEM_SIZE];

        this.setGCState(State.Dead);
    }

    public StackValue get(int index) {
        if (index > capacity - 1) {
            throw new IndexOutOfBoundsException();
        }
        return new StackValue(getBytes(HEADER_SIZE + index * ITEM_SIZE));
    }

    public void set(int index, StackValue value) {
        if (index > capacity - 1) {
            throw new IndexOutOfBoundsException();
        }

        setBytes(HEADER_SIZE + index * ITEM_SIZE, value.getBytes());
    }

    public int getSize() {
        return this.capacity;
    }

    /*public byte[] getItemsBytes(){
        byte[] bytes = new byte[getSize()*ITEM_SIZE];
        for (int i = 0; i<getSize(); i+=1){
             byte[] value = get(i).getValue();
             for (int j = 0; i<getSize(); i+=1) {

             }
        }

        return bytes;
    }*/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(super.toString() + "\n");

        for (int i = 0; i < capacity; i++) {
            sb.append(get(i) + " ");
        }

        return sb.toString();
    }
}
