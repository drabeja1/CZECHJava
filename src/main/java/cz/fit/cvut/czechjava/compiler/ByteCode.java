package cz.fit.cvut.czechjava.compiler;

import java.util.*;

/**
 *
 * @author Jakub
 */
public class ByteCode {

    protected List<Instruction> instructions;

    public ByteCode() {
        instructions = new ArrayList<>();

    }

    public Instruction addInstruction(Instruction inst) {
        instructions.add(inst);
        return inst;
    }

    public Instruction getInstruction(int position) {
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
