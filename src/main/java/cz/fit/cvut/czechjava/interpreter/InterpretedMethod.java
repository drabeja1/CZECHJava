package cz.fit.cvut.czechjava.interpreter;

import cz.fit.cvut.czechjava.compiler.model.Method;

/**
 *
 * @author Jakub
 */
public class InterpretedMethod extends Method {

    int instructionPosition;

    public InterpretedMethod(Method method) {
        super(method.getName(), method.getArgs(), method.getClassName(), method.getReturnType());

        this.addFlags(method.getFlags());

        //Copy
        this.setByteCode(method.getByteCode());
        this.setLocalVariablesCount(method.getLocalVariablesCount());
    }

    public int getInstructionPosition() {
        return instructionPosition;
    }

    public void setInstructionPosition(int instructionPosition) {
        this.instructionPosition = instructionPosition;
    }
}
