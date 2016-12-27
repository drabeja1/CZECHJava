package cz.fit.cvut.czechjava.interpreter.natives.math;

import cz.fit.cvut.czechjava.interpreter.memory.Heap;
import cz.fit.cvut.czechjava.interpreter.natives.Native;
import cz.fit.cvut.czechjava.interpreter.StackValue;

/**
 *
 * @author Jakub
 */
public class PowInt extends Native {

    public PowInt(Heap heap) {
        super(heap);
    }

    @Override
    public StackValue invoke(StackValue[] args) {
        int value = args[0].intValue();
        int base = args[1].intValue();

        double res = Math.pow(value, base);
        int resInt = (int) res;

        return new StackValue(resInt, StackValue.Type.Primitive);
    }
}
