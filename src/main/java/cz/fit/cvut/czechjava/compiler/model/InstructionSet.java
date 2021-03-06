package cz.fit.cvut.czechjava.compiler.model;

/**
 * Set of possible instructions
 *
 * @author Jakub
 */
public enum InstructionSet {
    // Integer
    StoreInteger("istore"),
    LoadInteger("iload"),
    AddInteger("iadd"),
    SubtractInteger("isub"),
    MultiplyInteger("imul"),
    DivideInteger("idiv"),
    ModuloInteger("irem"),
    PushInteger("ipush"),
    IntegerToFloat("i2f"),
    // Float
    StoreFloat("fstore"),
    LoadFloat("fload"),
    AddFloat("fadd"),
    SubtractFloat("fsub"),
    MultiplyFloat("fmul"),
    DivideFloat("fdiv"),
    ModuloFloat("frem"),
    PushFloat("fpush"),
    FloatToInteger("f2i"),
    // Reference
    StoreReference("astore"),
    LoadReference("aload"),
    PushConstant("ldc"),
    // Array
    NewArray("newarray"),
    StoreIntegerArray("iastore"),
    StoreReferenceArray("aastore"),
    LoadIntegerArray("iaload"),
    LoadReferenceArray("aaload"),
    // Object
    New("new"),
    Duplicate("dup"),
    GetField("getfield"),
    PutField("putfield"),
    // Compare Integer
    IfCompareEqualInteger("if_icmpeq"),
    IfCompareNotEqualInteger("if_icmpne"),
    IfCompareGreaterThanOrEqualInteger("if_icmpge"),
    IfCompareGreaterThanInteger("if_icmpgt"),
    IfCompareLessThanOrEqualInteger("if_icmple"),
    IfCompareLessThanInteger("if_icmplt"),
    // Compare Float
    FloatCompare("fcmpl"),
    IfEqualZero("ifeq"),
    IfNotEqualZero("ifne"),
    IfLessThanZero("iflt"),
    IfLessOrEqualThanZero("ifle"),
    IfGreaterThanZero("ifgt"),
    IfGreaterOrEqualThanZero("ifge"),
    // Calls
    GoTo("goto"),
    InvokeVirtual("invokevirtual"),
    InvokeStatic("invokestatic"),
    InvokeSpecial("invokespecial"),
    ReturnReference("areturn"),
    ReturnInteger("ireturn"),
    ReturnVoid("return"),
    // Debug
    Breakpoint("int");

    private final String abbreviation;

    InstructionSet(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * Vrati zkratku instrukce.
     * 
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return abbreviation;
    }

    /**
     * Get instruction form string
     *
     * @param text
     * @return instruction, or {@code null}
     */
    public static InstructionSet fromString(String text) {
        if (text != null) {
            for (InstructionSet b : InstructionSet.values()) {
                if (text.equalsIgnoreCase(b.abbreviation)) {
                    return b;
                }
            }
        }
        return null;
    }
}
