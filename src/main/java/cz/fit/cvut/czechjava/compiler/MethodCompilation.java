package cz.fit.cvut.czechjava.compiler;

import cz.fit.cvut.czechjava.compiler.model.ByteCode;
import cz.fit.cvut.czechjava.compiler.model.Variable;
import cz.fit.cvut.czechjava.type.Type;

import java.util.*;

/**
 *
 * @author Jakub
 */
public class MethodCompilation {

    private final ByteCode byteCode;
    private final Map<String, Variable> localVars;

    public MethodCompilation() {
        byteCode = new ByteCode();
        localVars = new LinkedHashMap<>();
    }

    public ByteCode getByteCode() {
        return byteCode;
    }

    public int addLocalVariable(String name, Type type) {
        name = name.toLowerCase();

        if (localVars.containsKey(name)) {
            return -1;
        }

        Variable var = new Variable(name, type);

        int pos = localVars.size();
        localVars.put(name, var);
        return pos;
    }

    public Type getTypeOfLocalVariable(String name) {
        name = name.toLowerCase();
        Variable var = localVars.get(name);
        return var.getType();
    }

    public Variable getLocalVariable(String name) {
        name = name.toLowerCase();
        return localVars.get(name);
    }

    public int getNumberOfLocalVariables() {
        return localVars.size();
    }

    public Variable getLocalVariable(int index) {
        return localVars.get(variablesKeySet().get(index));
    }

    public int getPositionOfLocalVariable(String name) {
        name = name.toLowerCase();
        return variablesKeySet().indexOf(name);
    }

    protected List<String> variablesKeySet() {
        return new ArrayList<>(localVars.keySet());
    }
}
