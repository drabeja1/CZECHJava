package cz.fit.cvut.czehjava.test;

import cz.fit.cvut.czechjava.Globals;
import cz.fit.cvut.czechjava.interpreter.memory.SimpleHeap;
import org.junit.Test;

/**
 *
 * @author Jakub
 */
public class HeapTest {
    
    @Test
    public void testHeap() {
        SimpleHeap sh = new SimpleHeap(Globals.HEAP_SIZE_DEFAULS);
        
    }
}
