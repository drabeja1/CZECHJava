package cz.fit.cvut.czechjava.interpreter.memory;

import cz.fit.cvut.czechjava.interpreter.ByteArrayWrapper;
import cz.fit.cvut.czechjava.interpreter.memory.garbagecollector.State;

/**
 *
 * @author Jakub
 */
public abstract class HeapItem extends ByteArrayWrapper {

    final int GC_STATE_SIZE = 1;

    public final void setGCState(State state) {
        byte b = 0;
        if (state == State.Live) {
            b = 1;
        }

        byteArray[0] = b;
    }

    public State getGCState() {
        byte b = byteArray[0];
        return (b == 1) ? State.Live : State.Dead;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "GC: " + getGCState();
    }

}
