package cz.fit.cvut.czechjava.interpreter;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ExplodeLoop;
import com.oracle.truffle.api.nodes.RootNode;
import cz.fit.cvut.czechjava.interpreter.exceptions.LookupException;
import cz.fit.cvut.czechjava.interpreter.exceptions.InterpreterException;
import cz.fit.cvut.czechjava.Globals;
import cz.fit.cvut.czechjava.compiler.model.Class;
import cz.fit.cvut.czechjava.compiler.model.ConstantPool;
import cz.fit.cvut.czechjava.compiler.model.Instruction;
import cz.fit.cvut.czechjava.compiler.model.InstructionSet;
import cz.fit.cvut.czechjava.compiler.model.Method;
import cz.fit.cvut.czechjava.type.Type;
import cz.fit.cvut.czechjava.interpreter.memory.Array;
import cz.fit.cvut.czechjava.interpreter.memory.garbagecollector.impl.GenerationCollector;
import cz.fit.cvut.czechjava.interpreter.memory.GenerationHeap;
import cz.fit.cvut.czechjava.interpreter.exceptions.HeapOverflowException;
import cz.fit.cvut.czechjava.interpreter.memory.Object;
import cz.fit.cvut.czechjava.interpreter.natives.Natives;
import cz.fit.cvut.czechjava.type.ArrayType;
import cz.fit.cvut.czechjava.type.Types;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Jakub
 */
public class CZECHJavaInterpreter extends RootNode {

    /**
     * Logger
     */
    private static final Logger LOGGER = Logger.getLogger(CZECHJavaInterpreter.class.getName());

    private final static int END_RETURN_ADDRESS = -1;

    private final ClassPool classPool;
    private final ConstantPool constantPool;
    private final Stack stack;
    private final GenerationHeap heap;
    private final Instructions instructions;
    private final Natives natives;
    private final List<String> arguments;

    public CZECHJavaInterpreter(List<Class> compiledClasses, int heapSize, int frameNumber, int stackSize, List<String> arguments)
            throws InterpreterException, LookupException {

        this.arguments = arguments;
        this.stack = new Stack(frameNumber, stackSize);
        this.classPool = new ClassPool(compiledClasses);
        this.heap = new GenerationHeap((int) (heapSize * 0.1), (int) (heapSize * 0.9), stack);
        this.heap.setGarbageCollector(new GenerationCollector(stack, heap));
        this.instructions = new Instructions(classPool);
        this.constantPool = new ConstantPool(classPool);
        this.natives = new Natives(this.heap, classPool);

        Debugger.init(heap, classPool, constantPool);
    }

    /**
     * {@inheritDoc}
     */
    @ExplodeLoop
    @Override
    public Object execute(VirtualFrame frame) {
        try {
            run(arguments);
        } catch (InterpreterException | HeapOverflowException ex) {
            LOGGER.fatal(ex);
            throw new RuntimeException(ex);
        }

        return null;
    }

    public void run(List<String> arguments) throws InterpreterException, HeapOverflowException {
        InterpretedClass mainClass = null;
        Method mainMethod = null;

        try {
            mainClass = classPool.lookupClass(Globals.MAIN_CLASS_NAME);
        } catch (LookupException e) {
            LOGGER.fatal("Main class not found.", e);
            throw new InterpreterException("Main class not found. The name has to be '" + Globals.MAIN_CLASS_NAME + "'");
        }

        for (Method method : mainClass.getMethods()) {
            if (method.getName().equals(Globals.MAIN_METHOD_NAME)) {
                mainMethod = method;
            }
        }

        if (mainMethod == null) {
            LOGGER.fatal("Main method not found. The name has to be '" + Globals.MAIN_METHOD_NAME + "'");
            throw new InterpreterException("Main method not found. The name has to be '" + Globals.MAIN_METHOD_NAME + "'");
        }

        StackValue objectPointer = heap.allocObject(mainClass);
        // Create new frame for main method
        stack.newFrame(END_RETURN_ADDRESS, objectPointer, mainMethod);

        // Pushing arguments to the main method local variables
        pushArguments(stack.currentFrame(), mainMethod, arguments);

        // Find instructions for main method
        int mainMethodPosition = ((InterpretedMethod) mainMethod).getInstructionPosition();
        interpret(mainMethodPosition);
    }

    public void interpret(int startingPosition) throws InterpreterException, HeapOverflowException {
        instructions.goTo(startingPosition);
        Iterator<Instruction> itr = instructions.getIterator();

        // Move from one instruction to next
        while (itr.hasNext()) {
            Instruction instruction = itr.next();
            executeInstruction(instruction, stack);
        }
    }

    protected void pushArguments(Frame frame, Method mainMethod, List<String> arguments) throws HeapOverflowException, InterpreterException {
        List<Type> args = mainMethod.getArgs();
        int i = 0;
        for (Type argType : args) {
            if (i >= arguments.size()) {
                break;
            }

            StackValue argValue = null;
            String stringArgValue = arguments.get(i);

            // Main method can get either number, char array or float as argument
            // It depends on type of arguments in the main method
            if (argType == Types.Number()) {
                argValue = new StackValue(Integer.parseInt(stringArgValue), StackValue.Type.Primitive);
            } else if (argType == Types.Float()) {
                argValue = new StackValue(Float.parseFloat(stringArgValue));
            } else if (argType == Types.CharArray()) {
                Array array = TypeConverter.charArrayToArray(stringArgValue.toCharArray());
                argValue = heap.alloc(array);
            } else {
                throw new InterpreterException("Unsupported argument type '" + argType + "' in " + mainMethod);
            }

            // 0 reserved for this
            frame.storeVariable(i + 1, argValue);
            i++;
        }
    }

    // INSTRUCTIONS EXECUTION
    public void executeInstruction(Instruction instruction, Stack stack) throws InterpreterException, HeapOverflowException {
        switch (instruction.getInstruction()) {
            case PushInteger:
            case PushFloat:
            case StoreInteger:
            case LoadInteger:
            case StoreReference:
            case LoadReference:
            case LoadFloat:
            case StoreFloat:
                executeStackInstruction(instruction, stack);
                break;
            case AddInteger:
            case SubtractInteger:
            case MultiplyInteger:
            case DivideInteger:
            case ModuloInteger:
                executeIntegerArithmeticInstruction(instruction, stack);
                break;
            case AddFloat:
            case SubtractFloat:
            case MultiplyFloat:
            case DivideFloat:
            case ModuloFloat:
                executeFloatArithmeticInstruction(instruction, stack);
                break;
            case IfCompareEqualInteger:
            case IfCompareNotEqualInteger:
            case IfCompareLessThanInteger:
            case IfCompareLessThanOrEqualInteger:
            case IfCompareGreaterThanInteger:
            case IfCompareGreaterThanOrEqualInteger:
                executeIntegerCompareInstruction(instruction, stack);
                break;
            case IfEqualZero:
            case IfNotEqualZero:
            case IfLessThanZero:
            case IfLessOrEqualThanZero:
            case IfGreaterThanZero:
            case IfGreaterOrEqualThanZero:
                executeZeroCompareInstruction(instruction, stack);
                break;
            case FloatCompare:
                executeFloatCompareInstruction(instruction, stack);
                break;
            case GoTo:
                executeGoToInstruction(instruction, stack);
                break;
            case ReturnVoid:
            case ReturnReference:
            case ReturnInteger:
                executeReturnInstruction(instruction, stack);
                break;
            case InvokeVirtual:
            case InvokeStatic:
            case InvokeSpecial:
                executeInvokeInstruction(instruction, stack);
                break;
            case Duplicate:
                executeDuplicateInstruction(instruction, stack);
                break;
            case New:
                executeNewInstruction(instruction, stack);
                break;
            case GetField:
            case PutField:
                executeFieldInstruction(instruction, stack);
                break;
            case Breakpoint:
                // Place breakpoint here
                break;
            case PushConstant:
                executeStringInstruction(instruction, stack);
                break;
            case NewArray:
            case StoreIntegerArray:
            case LoadIntegerArray:
            case StoreReferenceArray:
            case LoadReferenceArray:
                executeArrayInstruction(instruction, stack);
                break;
            case FloatToInteger:
            case IntegerToFloat:
                executeConvertInstruction(instruction, stack);
                break;
            default:
                throw new UnsupportedOperationException();
        }

    }

    public void executeArrayInstruction(Instruction instruction, Stack stack) throws InterpreterException, HeapOverflowException {
        switch (instruction.getInstruction()) {
            case NewArray:
                int size = stack.currentFrame().pop().intValue();
                StackValue reference = heap.allocArray(size);
                stack.currentFrame().push(reference);
                break;
            case StoreIntegerArray:
            case StoreReferenceArray: {
                StackValue value = stack.currentFrame().pop();
                int index = stack.currentFrame().pop().intValue();
                StackValue arrayRef = stack.currentFrame().pop();

                if (instruction.getInstruction() == InstructionSet.StoreReferenceArray) {
                    heap.addDirtyLink(arrayRef, value);
                }

                Array array = heap.loadArray(arrayRef);
                array.set(index, value);
            }
            break;
            case LoadIntegerArray:
            case LoadReferenceArray: {
                int index = stack.currentFrame().pop().intValue();
                StackValue arrayRef = stack.currentFrame().pop();
                Array array = heap.loadArray(arrayRef);
                StackValue value = array.get(index);
                stack.currentFrame().push(value);
            }
            break;
        }
    }

    public void executeStringInstruction(Instruction instruction, Stack stack) throws InterpreterException, HeapOverflowException {
        int constPosition = instruction.getOperand(0);
        String constant = constantPool.getConstant(constPosition);
        // Create array of chars and push it on stack
        Array charArray = TypeConverter.charArrayToArray(constant.toCharArray());
        StackValue reference = heap.alloc(charArray);

        stack.currentFrame().push(reference);
    }

    public void executeNewInstruction(Instruction instruction, Stack stack) throws InterpreterException, HeapOverflowException {
        int constPosition = instruction.getOperand(0);
        String className = constantPool.getConstant(constPosition);

        try {
            InterpretedClass objectClass = classPool.lookupClass(className);
            StackValue reference = heap.allocObject(objectClass);
            stack.currentFrame().push(reference);
        } catch (LookupException e) {
            LOGGER.fatal(e);
            throw new InterpreterException("Trying to instantiate non-existent class '" + className + "'");
        }
    }

    public void executeDuplicateInstruction(Instruction instruction, Stack stack) throws InterpreterException {
        StackValue value = stack.currentFrame().pop();
        stack.currentFrame().push(value);
        stack.currentFrame().push(value);
    }

    public void executeFieldInstruction(Instruction instruction, Stack stack) throws InterpreterException {
        StackValue value = null;

        // If we are setting we must first pop the value
        if (instruction.getInstruction() == InstructionSet.PutField) {
            value = stack.currentFrame().pop();
        }

        // Get object and find the field
        StackValue reference = stack.currentFrame().pop();
        int constPosition = instruction.getOperand(0);
        String fieldName = constantPool.getConstant(constPosition);
        cz.fit.cvut.czechjava.interpreter.memory.Object object = heap.loadObject(reference);

        InterpretedClass objectClass = object.loadClass(classPool);
        try {
            int fieldPosition = objectClass.lookupField(fieldName);
            switch (instruction.getInstruction()) {
                case GetField:
                    stack.currentFrame().push(object.getField(fieldPosition));
                    break;
                case PutField:
                    heap.addDirtyLink(reference, value);
                    object.setField(fieldPosition, value);
                    break;
            }
        } catch (LookupException e) {
            throw new InterpreterException("Trying to access non-existent field '" + fieldName + "'");
        }
    }

    public void executeInvokeInstruction(Instruction instruction, Stack stack) throws InterpreterException, HeapOverflowException {
        InstructionSet inst = instruction.getInstruction();
        int constPosition = instruction.getOperand(0);
        String methodDescriptor = constantPool.getConstant(constPosition);
        StackValue objectRef;

        try {
            objectRef = stack.currentFrame().pop();
            InterpretedClass objectClass;

            if (inst == InstructionSet.InvokeVirtual) {
                // Get class from the actual object on heap
                Object object = heap.loadObject(objectRef);
                objectClass = object.loadClass(classPool);
                // Static & special
            } else {
                // Get class from descriptor
                String className = new Method(methodDescriptor).getClassName();
                objectClass = classPool.lookupClass(className);
            }

            // Lookup real interpreted method
            InterpretedMethod method = objectClass.lookupMethod(methodDescriptor, classPool);
            if (method.isNativeMethod()) {
                invokeNative(methodDescriptor);
                return;
            }

            // Return to next instruction
            int returnAddress = instructions.getCurrentPosition() + 1;

            // Pop arguments from the caller stack
            int numberOfArgs = method.getArgs().size();
            StackValue[] argValues = new StackValue[numberOfArgs];

            for (int i = 0; i < numberOfArgs; i++) {
                argValues[i] = stack.currentFrame().pop();
            }

            stack.newFrame(returnAddress, objectRef, method);

            // Store arguments as variables in callee stack
            for (int i = 0; i < numberOfArgs; i++) {
                // Start with 1 index, 0 is reserved for This
                stack.currentFrame().storeVariable(i + 1, argValues[i]);
            }

            // Go to the method bytecode start
            instructions.goTo(((InterpretedMethod) method).getInstructionPosition());
        } catch (LookupException e) {
            throw new InterpreterException("Trying to call non-existent method '" + methodDescriptor + "'");
        }
    }

    public void invokeNative(String methodDescriptor) throws InterpreterException, HeapOverflowException {
        if (!natives.nativeExist(methodDescriptor)) {
            throw new InterpreterException("Trying to call non-existent method '" + methodDescriptor + "'");
        }

        // Load approximate method from descriptor so we can count the arguments
        Method methodFromDescriptor = new Method(methodDescriptor);
        int numberOfArgs = methodFromDescriptor.getArgs().size();
        StackValue[] argValues = new StackValue[numberOfArgs];

        for (int i = 0; i < methodFromDescriptor.getArgs().size(); i++) {
            Type type = methodFromDescriptor.getArgs().get(i);

            if (type instanceof ArrayType || type == Types.Number() || type == Types.Char() || type == Types.Boolean() || type == Types.Float()) {
                StackValue value = stack.currentFrame().pop();
                argValues[i] = value;
            } else {
                throw new InterpreterException("Passing " + type + " in native functions is not supported");
            }
        }

        StackValue returnValue = natives.invoke(methodDescriptor, argValues);
        if (returnValue != null) {
            stack.currentFrame().push(returnValue);
        }
    }

    public void executeReturnInstruction(Instruction instruction, Stack stack) throws InterpreterException {
        int returnAddress = stack.currentFrame().getReturnAddress();
        switch (instruction.getInstruction()) {
            case ReturnVoid:
                stack.deleteCurrentFrame();
                break;
            case ReturnInteger:
            case ReturnReference: {
                StackValue var = stack.currentFrame().pop();
                // Remove current frame
                stack.deleteCurrentFrame();
                // Push return value on the calling frame
                stack.currentFrame().push(var);
            }
            break;
        }
        instructions.goTo(returnAddress);
    }

    public void executeStackInstruction(Instruction instruction, Stack stack) throws InterpreterException {
        switch (instruction.getInstruction()) {
            case PushInteger: {
                StackValue value = new StackValue(instruction.getOperand(0), StackValue.Type.Primitive);
                stack.currentFrame().push(value);
            }
            break;
            case PushFloat: {
                int constantIndex = instruction.getOperand(0);
                String floatString = constantPool.getConstant(constantIndex);
                StackValue value = new StackValue(floatString);
                stack.currentFrame().push(value);
            }
            break;
            case StoreInteger:
            case StoreFloat: {
                StackValue var = stack.currentFrame().pop();
                stack.currentFrame().storeVariable(instruction.getOperand(0), var);
            }
            break;
            case LoadInteger:
            case LoadFloat:
            case LoadReference: {
                StackValue var = stack.currentFrame().loadVariable(instruction.getOperand(0));
                stack.currentFrame().push(var);
            }
            break;
            case StoreReference: {
                StackValue var = stack.currentFrame().pop();
                StackValue reference = new StackValue(var.intValue(), StackValue.Type.Pointer);
                stack.currentFrame().storeVariable(instruction.getOperand(0), reference);
            }
            break;
        }
    }

    public void executeIntegerArithmeticInstruction(Instruction instruction, Stack stack) throws InterpreterException {
        int b = stack.currentFrame().pop().intValue();
        int a = stack.currentFrame().pop().intValue();
        int result = 0;

        switch (instruction.getInstruction()) {
            case AddInteger:
                result = a + b;
                break;
            case SubtractInteger:
                result = a - b;
                break;
            case MultiplyInteger:
                result = a * b;
                break;
            case DivideInteger:
                if (b == 0) {
                    throw new InterpreterException("Division by zero");
                }
                result = a / b;
                break;
            case ModuloInteger:
                result = a % b;
                break;
        }
        StackValue value = new StackValue(result, StackValue.Type.Primitive);
        stack.currentFrame().push(value);
    }

    public void executeFloatArithmeticInstruction(Instruction instruction, Stack stack) throws InterpreterException {
        float b = stack.currentFrame().pop().floatValue();
        float a = stack.currentFrame().pop().floatValue();
        float result = 0;

        switch (instruction.getInstruction()) {
            case AddFloat:
                result = a + b;
                break;
            case SubtractFloat:
                result = a - b;
                break;
            case MultiplyFloat:
                result = a * b;
                break;
            case DivideFloat:
                if (b == 0) {
                    throw new InterpreterException("Division by zero");
                }
                result = a / b;
                break;
            case ModuloFloat:
                result = a % b;
                break;
        }
        StackValue value = new StackValue(result);
        stack.currentFrame().push(value);
    }

    public void executeIntegerCompareInstruction(Instruction instruction, Stack stack) throws InterpreterException {
        int b = stack.currentFrame().pop().intValue();
        int a = stack.currentFrame().pop().intValue();
        int operand = instruction.getOperand(0);
        boolean condition = false;

        switch (instruction.getInstruction()) {
            case IfCompareEqualInteger:
                condition = (a == b);
                break;
            case IfCompareNotEqualInteger:
                condition = (a != b);
                break;
            case IfCompareLessThanInteger:
                condition = (a < b);
                break;
            case IfCompareLessThanOrEqualInteger:
                condition = (a <= b);
                break;
            case IfCompareGreaterThanInteger:
                condition = (a > b);
                break;
            case IfCompareGreaterThanOrEqualInteger:
                condition = (a >= b);
                break;
        }

        if (condition) {
            instructions.goTo(operand);
        }
    }

    public void executeZeroCompareInstruction(Instruction instruction, Stack stack) throws InterpreterException {
        int a = stack.currentFrame().pop().intValue();
        int operand = instruction.getOperand(0);
        boolean condition = false;

        switch (instruction.getInstruction()) {
            case IfEqualZero:
                condition = (a == 0);
                break;
            case IfNotEqualZero:
                condition = (a != 0);
                break;
            case IfLessThanZero:
                condition = (a < 0);
                break;
            case IfLessOrEqualThanZero:
                condition = (a <= 0);
                break;
            case IfGreaterThanZero:
                condition = (a > 0);
                break;
            case IfGreaterOrEqualThanZero:
                condition = (a >= 0);
                break;
        }

        if (condition) {
            instructions.goTo(operand);
        }
    }

    public void executeFloatCompareInstruction(Instruction instruction, Stack stack) throws InterpreterException {
        float b = stack.currentFrame().pop().floatValue();
        float a = stack.currentFrame().pop().floatValue();

        int result;
        if (a < b) {
            result = -1;
        } else if (a > b) {
            result = 1;
        } else {
            result = 0;
        }

        StackValue value = new StackValue(result, StackValue.Type.Primitive);
        stack.currentFrame().push(value);
    }

    public void executeConvertInstruction(Instruction instruction, Stack stack) throws InterpreterException {
        StackValue value = stack.currentFrame().pop();
        StackValue convertedValue = null;

        switch (instruction.getInstruction()) {
            case FloatToInteger:
                float floatValue = value.floatValue();
                convertedValue = new StackValue(TypeConverter.floatToInt(floatValue), StackValue.Type.Primitive);
                break;
            case IntegerToFloat:
                int intValue = value.intValue();
                convertedValue = new StackValue(TypeConverter.intToFloat(intValue));
                break;
        }

        stack.currentFrame().push(convertedValue);
    }

    public void executeGoToInstruction(Instruction instruction, Stack stack) throws InterpreterException {
        int jumpTo = instruction.getOperand(0);
        instructions.goTo(jumpTo);
    }
}
