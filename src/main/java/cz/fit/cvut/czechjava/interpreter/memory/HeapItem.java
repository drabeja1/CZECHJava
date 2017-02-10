package cz.fit.cvut.czechjava.interpreter.memory;

import cz.fit.cvut.czechjava.interpreter.ByteArrayWrapper;
import cz.fit.cvut.czechjava.interpreter.memory.garbagecollector.State;

/**
 *
 * @author Jakub
 */
public abstract class HeapItem extends ByteArrayWrapper {

    protected final static int GC_STATE_SIZE = 1;

    public final void setGCState(State state) {
        if (state.equals(State.Live)) {
            byteArray[0] = 1;
        } else {
            byteArray[0] = 0;
        }
    }

    public State getGCState() {
        return (byteArray[0] == 1) ? State.Live : State.Dead;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "GC: " + getGCState();
    }

}
