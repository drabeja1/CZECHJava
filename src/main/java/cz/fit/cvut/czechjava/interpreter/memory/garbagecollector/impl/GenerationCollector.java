package cz.fit.cvut.czechjava.interpreter.memory.garbagecollector.impl;

import cz.fit.cvut.czechjava.interpreter.Frame;
import cz.fit.cvut.czechjava.interpreter.memory.Array;
import cz.fit.cvut.czechjava.interpreter.memory.DirtyLink;
import cz.fit.cvut.czechjava.interpreter.memory.GenerationHeap;
import cz.fit.cvut.czechjava.interpreter.memory.HeapItem;
import cz.fit.cvut.czechjava.interpreter.exceptions.HeapOverflowException;
import cz.fit.cvut.czechjava.interpreter.memory.Object;
import cz.fit.cvut.czechjava.interpreter.memory.SimpleHeap;
import cz.fit.cvut.czechjava.interpreter.Stack;
import cz.fit.cvut.czechjava.interpreter.StackValue;
import cz.fit.cvut.czechjava.interpreter.memory.garbagecollector.GarbageCollector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author Jakub
 */
public class GenerationCollector extends GarbageCollector {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(GenerationCollector.class.getName());

    private final GenerationHeap heap;
    private final Stack stack;
    private final List<DirtyLink> generationDirtyLinks;

    public GenerationCollector(Stack stack, GenerationHeap heap) {
        this.heap = heap;
        this.stack = stack;
        this.generationDirtyLinks = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<StackValue> run(Set<StackValue> roots) throws HeapOverflowException {
        LOGGER.info("Collecting Eden");
        Set<StackValue> stackRoots = GarbageCollector.getRootsFromStack(stack);
        Set<StackValue> tenureDirtyLinks = collect(heap.getEden(), stackRoots, dirtyLinksToRoots());

        // if there is not enough space in tenure, clean it
        if (tenureOutOfSpace()) {
            LOGGER.info("Collecting Tenure");
            collect(heap.getTenure(), stackRoots, tenureDirtyLinks);

            // If still full - overflow
            if (tenureOutOfSpace()) {
                throw new HeapOverflowException();
            }
        }

        // Move all survivors to tenure and clear dirty links
        moveEdenToTenure();
        generationDirtyLinks.clear();

        return null;
    }

    public void addDirtyLink(StackValue from, StackValue reference) {
        if (!reference.isPointer() || reference.isNullPointer()) {
            return;
        }

        if (heap.isEdenReference(from)) {
            // We will find the eden -> tenure dirty links in garbage collection
            return;
        }

        // it's tenure object
        // assigning reference to eden
        if (heap.isEdenReference(reference)) {
            generationDirtyLinks.add(new DirtyLink(from, reference));
        }
    }

    protected Set<StackValue> dirtyLinksToRoots() {
        Set<StackValue> roots = new HashSet<>();
        LOGGER.info("Eden dirty links: ");

        generationDirtyLinks.forEach(link -> {
            LOGGER.info(link.getFrom() + "~" + link.getReference() + ", ");
            roots.add(link.getReference());
        });

        return roots;
    }

    protected boolean tenureOutOfSpace() {
        return heap.getTenure().spaceLeft() <= heap.getEden().spaceUsed();
    }

    protected Set<StackValue> collect(SimpleHeap heapToCollect, Set<StackValue> stackRoots, Set<StackValue> dirtyLinks) throws HeapOverflowException {
        LOGGER.info("Roots: " + stackRoots);
        LOGGER.info("Dirty links: " + dirtyLinks);

        // Run eden garbage collection on stack roots
        Set<StackValue> roots = new HashSet<>();
        roots.addAll(stackRoots);
        roots.addAll(dirtyLinks);

        int used = heapToCollect.spaceUsed();
        Set<StackValue> heapDirtyLinks = heapToCollect.getGarbageCollector().run(roots);
        int collected = used - heapToCollect.spaceUsed();
        
        LOGGER.info("Collected " + collected + " items");
        return heapDirtyLinks;
    }

    protected void moveEdenToTenure() throws HeapOverflowException {
        LOGGER.info("Moving to tenure");
        
        StackValue[] referenceMap = new StackValue[heap.getEdenSize()];
        for (int i = 0; i < heap.getEdenSize(); i++) {
            StackValue oldReference = heap.getEden().indexToReference(i);
            HeapItem obj = heap.getEden().load(oldReference);

            if (obj != null) {
                //Passing as reference, no need to deep copy
                StackValue newReference = heap.getTenure().alloc(obj);
                referenceMap[heap.getEden().referenceToIndex(oldReference)] = newReference;
                LOGGER.info(oldReference + ", ");

                heap.getEden().dealloc(oldReference);
            }
        }

        LOGGER.info("Translating eden");
        translateReferencesFromEden(referenceMap);

        LOGGER.info("Translating stack");
        translateReferencesOnStack(referenceMap);

        LOGGER.info("Translating dirty links");
        translateDirtyReferences(referenceMap);
    }

    protected void translateReferencesFromEden(StackValue[] referenceMap) {
        for (StackValue objRef : referenceMap) {
            if (objRef == null) {
                continue;
            }
            
            HeapItem heapObj = heap.getTenure().load(objRef);
            if (heapObj instanceof Object) {
                Object obj = (Object) heapObj;
                translateReferencesInObject(obj, referenceMap);
            } else if (heapObj instanceof Array) {
                translateReferencesInArray((Array) heapObj, referenceMap);
            }
        }

    }

    protected void translateReferencesInObject(Object obj, StackValue[] referenceMap) {
        for (int i = 0; i < obj.getFieldsNumber(); i++) {
            StackValue fieldValue = obj.getField(i);

            if (fieldValue.isPointer() && !fieldValue.isNullPointer()) {
                // If it points to tenure, there is nothing to translate
                if (heap.isEdenReference(fieldValue)) {
                    StackValue newRef = translateReference(fieldValue, referenceMap);
                    obj.setField(i, newRef);
                }
            }
        }
    }

    protected void translateReferencesInArray(Array array, StackValue[] referenceMap) {
        for (int i = 0; i < array.getSize(); i++) {
            StackValue value = array.get(i);

            if (value.isPointer() && !value.isNullPointer()) {
                // If it points to tenure, there is nothing to translate
                if (heap.isEdenReference(value)) {
                    StackValue newRef = translateReference(value, referenceMap);
                    array.set(i, newRef);
                }
            }
        }
    }

    protected void translateDirtyReferences(StackValue[] referenceMap) {
        generationDirtyLinks.forEach(link -> {
            LOGGER.info(link.getFrom() + "~" + link.getReference());
            HeapItem obj = heap.getTenure().load(link.getFrom());

            if (obj != null) {
                if (obj instanceof Object) {
                    translateReferencesInObject((Object) obj, referenceMap);
                } else {
                    translateReferencesInArray((Array) obj, referenceMap);
                }
            }
        });
    }

    protected StackValue translateReference(StackValue reference, StackValue[] referenceMap) {     
        StackValue translated = referenceMap[heap.getEden().referenceToIndex(reference)];
        LOGGER.info(reference + "->" + translated);
        return translated;
    }

    protected void translateReferencesOnStack(StackValue[] referenceMap) {
        for (int i = 0; i < stack.getFramesNumber(); i++) {
            Frame frame = stack.getFrame(i);

            for (int j = 0; j < frame.getLocalVariablesCount(); j++) {
                StackValue reference = frame.loadVariable(j);
                // Translate every variable on stack if it's eden reference
                if (reference.isPointer() && !reference.isNullPointer() && heap.isEdenReference(reference)) {
                    StackValue newRef = translateReference(reference, referenceMap);
                    frame.storeVariable(j, newRef);
                }
            }

            // There can also be references directly on stack
            for (int j = frame.getStackOffset(); j < frame.getSize(); j += StackValue.SIZE) {
                StackValue reference = frame.get(j);
                // Translate every value from stack if it's eden reference
                if (reference.isPointer() && !reference.isNullPointer() && heap.isEdenReference(reference)) {
                    StackValue newRef = translateReference(reference, referenceMap);
                    frame.set(j, newRef);
                }
            }
        }
    }
}
