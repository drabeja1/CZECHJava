package cz.fit.cvut.czehjava.test;

import cz.fit.cvut.czechjava.interpreter.StackValue;
import cz.fit.cvut.czechjava.interpreter.exceptions.HeapOverflowException;
import cz.fit.cvut.czechjava.interpreter.memory.Array;
import cz.fit.cvut.czechjava.interpreter.memory.SimpleHeap;
import cz.fit.cvut.czechjava.interpreter.memory.garbagecollector.impl.MarkAndSweepCollector;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Jakub
 */
public class HeapTest {

    /**
     * Test spravne alokace a nacitani ze SimpleHeap
     * 
     * @throws HeapOverflowException 
     */
    @Test
    public void testSimpleHeapAlloc() throws HeapOverflowException {
        Array array = new Array(3);
        array.set(0, new StackValue(1, StackValue.Type.Primitive));
        array.set(1, new StackValue(2, StackValue.Type.Primitive));
        array.set(2, new StackValue(3, StackValue.Type.Primitive));

        SimpleHeap sp = new SimpleHeap(5);
        MarkAndSweepCollector collector = new MarkAndSweepCollector(sp);
        sp.setGarbageCollector(collector);
        StackValue reference = sp.alloc(array);

        Assert.assertEquals(4, sp.spaceLeft());
        Assert.assertEquals(1, sp.spaceUsed());

        Array loadedArray = sp.loadArray(reference);
        Assert.assertEquals(3, loadedArray.getSize());
        Assert.assertEquals(1, loadedArray.get(0).intValue());
        Assert.assertEquals(2, loadedArray.get(1).intValue());
        Assert.assertEquals(3, loadedArray.get(2).intValue());
    }

    /**
     * Test preteceni SimpleHeap
     */
    @Test
    public void testSimpleHeapAllocHeapOverFlow() {
        Exception ex = null;
        try {
            Array array = new Array(3);
            array.set(0, new StackValue(1, StackValue.Type.Primitive));
            array.set(1, new StackValue(2, StackValue.Type.Primitive));
            array.set(2, new StackValue(3, StackValue.Type.Primitive));

            Array array2 = new Array(3);
            array2.set(0, new StackValue(1, StackValue.Type.Primitive));
            array2.set(1, new StackValue(2, StackValue.Type.Primitive));
            array2.set(2, new StackValue(3, StackValue.Type.Primitive));

            Array array3 = new Array(3);
            array3.set(0, new StackValue(1, StackValue.Type.Primitive));
            array3.set(1, new StackValue(2, StackValue.Type.Primitive));
            array3.set(2, new StackValue(3, StackValue.Type.Primitive));

            Array array4 = new Array(3);
            array4.set(0, new StackValue(1, StackValue.Type.Primitive));
            array4.set(1, new StackValue(2, StackValue.Type.Primitive));
            array4.set(2, new StackValue(3, StackValue.Type.Primitive));

            SimpleHeap sp = new SimpleHeap(2);
            MarkAndSweepCollector collector = new MarkAndSweepCollector(sp);
            sp.setGarbageCollector(collector);
            
            sp.alloc(array);
            sp.alloc(array2);
            sp.alloc(array3);
            sp.alloc(array4);
        } catch (HeapOverflowException e) {
            ex = e;
        }
        Assert.assertNotNull(ex);
    }
}
