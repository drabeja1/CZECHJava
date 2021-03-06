package cz.fit.cvut.czechjava.interpreter.memory.garbagecollector;

import cz.fit.cvut.czechjava.interpreter.Frame;
import cz.fit.cvut.czechjava.interpreter.exceptions.HeapOverflowException;
import cz.fit.cvut.czechjava.interpreter.Stack;
import cz.fit.cvut.czechjava.interpreter.StackValue;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Jakub
 */
public abstract class GarbageCollector {

    /**
     * Return roots from stack
     *
     * @param stack stack
     * @return roots
     */
    public static Set<StackValue> getRootsFromStack(Stack stack) {
        Set<StackValue> roots = new HashSet<>();

        // Go through all frames and get references
        for (int i = 0; i < stack.getFramesNumber(); i++) {
            Frame frame = stack.getFrame(i);

            for (int j = 0; j < frame.getLocalVariablesCount(); j++) {
                StackValue reference = frame.loadVariable(j);
                if (reference.isPointer() && !reference.isNullPointer()) {
                    roots.add(reference);
                }
            }

            // There can also be references directly on stack
            for (int j = frame.getStackOffset(); j < frame.getSize(); j += StackValue.SIZE) {
                StackValue reference = frame.get(j);
                // Translate every value from stack if it's eden reference
                if (reference.isPointer() && !reference.isNullPointer()) {
                    roots.add(reference);
                }
            }
        }

        return roots;
    }

    /**
     * Returns dirty links
     *
     * @param roots
     * @return
     * @throws HeapOverflowException dirty links
     */
    public abstract Set<StackValue> run(Set<StackValue> roots) throws HeapOverflowException;
}
