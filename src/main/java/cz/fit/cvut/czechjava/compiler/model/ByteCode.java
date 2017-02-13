package cz.fit.cvut.czechjava.compiler.model;

import com.oracle.truffle.api.nodes.Node;
import java.util.*;

/**
 *
 * @author Jakub
 */
public class ByteCode extends Node {

    /**
     * List of instructuons
     */
    protected List<Instruction> instructions;

    public ByteCode() {
        instructions = new ArrayList<>();
    }

    public Instruction addInstruction(Instruction inst) {
        instructions.add(inst);
        return inst;
    }

    /**
     * 
     * @param position
     * @return instruction at position, or {@code null} 
     */
    public Instruction getInstruction(int position) {
        if (position < 0 || position >= instructions.size()) {
            return null;
        }
        return instructions.get(position);
    }

    public Instruction getLastInstruction() {
        return getInstruction(getLastInstructionPosition());
    }

    public int getLastInstructionPosition() {
        return instructions.size() - 1;
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        instructions.forEach(i -> sb.append(i).append(System.getProperty("line.separator")));    
        return sb.toString();
    }
}
