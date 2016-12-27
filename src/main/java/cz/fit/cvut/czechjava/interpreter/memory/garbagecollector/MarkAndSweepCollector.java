package cz.fit.cvut.czechjava.interpreter.memory.garbagecollector;

import cz.fit.cvut.czechjava.interpreter.memory.Array;
import cz.fit.cvut.czechjava.interpreter.memory.HeapItem;
import cz.fit.cvut.czechjava.interpreter.memory.Object;
import cz.fit.cvut.czechjava.interpreter.memory.SimpleHeap;
import cz.fit.cvut.czechjava.interpreter.StackValue;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jakub
 */
public class MarkAndSweepCollector extends GarbageCollector {

    private static final Logger log = Logger.getLogger(MarkAndSweepCollector.class.getName());
    SimpleHeap heap;

    public MarkAndSweepCollector(SimpleHeap heap) {
        super(heap);
        this.heap = heap;
    }

    public Set<StackValue> run(Set<StackValue> roots) {

        Set<StackValue> dirtyLinks = mark(roots);
        sweep();

        return dirtyLinks;
    }

    protected Set<StackValue> mark(Set<StackValue> roots) {
        Set<StackValue> dirtyLinks = new HashSet<>();

        for (StackValue rootRef : roots) {

            //We clean only stuff from this heap
            if (!heap.referenceIsOutOfBounds(rootRef)) {
                dirtyLinks.addAll(markObject(rootRef));
            }
        }

        return dirtyLinks;
    }

    protected Set<StackValue> markObject(StackValue reference) {
        Set<StackValue> dirtyLinks = new HashSet<>();

        if (reference.isPointer() && !reference.isNullPointer()) {
            //Reference is leading to different generation, don't follow, mark dirty link
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

        log.log(Level.FINE, "Collected: ");

        for (int i = 0; i < heap.getSize(); i++) {

            StackValue reference = heap.indexToReference(i);

            HeapItem obj = heap.load(reference);

            if (obj != null) {
                if (obj.getGCState() == State.Dead) {
                    log.log(Level.FINE, reference + ", ");
                    heap.dealloc(reference);
                } else {
                    //Reset all to dead state
                    obj.setGCState(State.Dead);
                }
            }

        }

    }

}
