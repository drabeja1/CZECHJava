package cz.fit.cvut.czechjava.interpreter.memory.garbagecollector.impl;

import cz.fit.cvut.czechjava.interpreter.memory.Array;
import cz.fit.cvut.czechjava.interpreter.memory.HeapItem;
import cz.fit.cvut.czechjava.interpreter.memory.Object;
import cz.fit.cvut.czechjava.interpreter.memory.SimpleHeap;
import cz.fit.cvut.czechjava.interpreter.StackValue;
import cz.fit.cvut.czechjava.interpreter.memory.garbagecollector.GarbageCollector;
import cz.fit.cvut.czechjava.interpreter.memory.garbagecollector.State;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public class MarkAndSweepCollector extends GarbageCollector {

    private static final Logger LOGGER = Logger.getLogger(MarkAndSweepCollector.class.getName());
    private final SimpleHeap heap;

    public MarkAndSweepCollector(SimpleHeap heap) {
        this.heap = heap;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public Set<StackValue> run(Set<StackValue> roots) {
        Set<StackValue> dirtyLinks = mark(roots);
        sweep();
        return dirtyLinks;
    }

    protected Set<StackValue> mark(Set<StackValue> roots) {
        Set<StackValue> dirtyLinks = new HashSet<>();

        roots.forEach(rootRef -> {
            if (!heap.referenceIsOutOfBounds(rootRef)) {
                dirtyLinks.addAll(markObject(rootRef));
            }
        });

        return dirtyLinks;
    }

    protected Set<StackValue> markObject(StackValue reference) {
        Set<StackValue> dirtyLinks = new HashSet<>();

        if (reference.isPointer() && !reference.isNullPointer()) {
            // Reference is leading to different generation, don't follow, mark dirty link
            if (heap.referenceIsOutOfBounds(reference)) {
                dirtyLinks.add(reference);
                return dirtyLinks;
            }

            HeapItem heapObj = heap.load(reference);
            heapObj.setGCState(State.Live);

            if (heapObj instanceof Object) {
                Object obj = (Object) heapObj;

                for (int i = 0; i < obj.getFieldsNumber(); i++) {
                    StackValue fieldRef = obj.getField(i);
                    dirtyLinks.addAll(markObject(fieldRef));
                }
            } else if (heapObj instanceof Array) {
                Array array = (Array) heapObj;

                for (int i = 0; i < array.getSize(); i++) {
                    StackValue itemRef = array.get(i);
                    dirtyLinks.addAll(markObject(itemRef));
                }
            }
        }

        return dirtyLinks;
    }

    public void sweep() {
        LOGGER.info("Collected: ");

        for (int i = 0; i < heap.getSize(); i++) {
            StackValue reference = heap.indexToReference(i);
            HeapItem obj = heap.load(reference);

            if (obj != null) {
                if (obj.getGCState() == State.Dead) {
                    LOGGER.log(Level.INFO, "{0}, ", reference);
                    heap.dealloc(reference);
                } else {
                    // Reset all to dead state
                    obj.setGCState(State.Dead);
                }
            }
        }
    }
}
