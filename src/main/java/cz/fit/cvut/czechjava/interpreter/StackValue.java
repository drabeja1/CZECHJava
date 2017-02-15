package cz.fit.cvut.czechjava.interpreter;

/**
 *
 * @author Jakub
 */
public class StackValue extends ByteArrayWrapper {

    /**
     * Stack value types
     */
    public enum Type {
        Primitive,
        Pointer;
    }

    private final int POINTER_LAST_BIT = 1;
    private final int FLOAT_BIAS = 127;
    private final int REDUCED_FLOAT_BIAS = 63;
    private final int MANTISA_SIZE = 23;

    public static final int SIZE = 4;

    public StackValue(byte[] bytes) {
        this.byteArray = bytes;
    }

    public StackValue(int number, Type type) {
        this.byteArray = integerToInnerRepresentation(number, type);
    }

    public StackValue(String floatString) {
        this(TypeConverter.stringToFloat(floatString));
    }

    public StackValue(Float floatNumber) {
        this.byteArray = floatToInnerRepresentation(floatNumber);
    }

    // Shifts integer by 1 so that the top bit signifies whether it's a pointer
    protected final byte[] integerToInnerRepresentation(int i, Type type) {
        int lastBit = (type == Type.Pointer) ? POINTER_LAST_BIT : 0;
        int pointer = i << 1 | lastBit;
        return TypeConverter.intToByteArray(pointer);
    }

    // Shifts float by 1, but has to reduce exponent to make space in float
    protected final byte[] floatToInnerRepresentation(float f) {
        byte[] bytes = TypeConverter.floatToByteArray(f);
        int i = TypeConverter.byteArrayToInt(bytes);

        // last 23 bits
        int mantisa = (i & 0x7FFFFF);
        // 8 bits after mantisa
        int exponent = (i & 0x7F800000) >> MANTISA_SIZE;
        
        // Make it unsigned
        exponent = exponent - FLOAT_BIAS + REDUCED_FLOAT_BIAS;
        
        // If the exponent is too big, throw exception
        if (exponent >= 128 || exponent < 0) {
            throw new IllegalArgumentException("Float overflow");
        }
        exponent <<= 23;

        // Shift by 1
        int sign = (i & 0x80000000) >> 1;
        int result = sign | exponent | mantisa;

        return TypeConverter.intToByteArray(result);
    }

    // Shifts bytes back
    public int innerRepresentationToIntValue() {
        int i = TypeConverter.byteArrayToInt(byteArray);
        int value = i >> 1;
        return value;
    }

    public float innerRepresentationToFloat(byte[] bytes) {

        int i = TypeConverter.byteArrayToInt(bytes);

        // 23 bits
        int mantisa = (i & 0x7FFFFF);
        // 7 bits after mantisa
        int exponent = (i & 0x3F800000) >> MANTISA_SIZE;
        // Change exponent back to the bias
        exponent = exponent - REDUCED_FLOAT_BIAS + FLOAT_BIAS;
        exponent <<= MANTISA_SIZE;

        int sign = (i & 0x80000000) << 1;
        int result = sign | exponent | mantisa;

        return TypeConverter.byteArrayToFloat(TypeConverter.intToByteArray(result));
    }

    public boolean isPointer() {
        return (byteArray[byteArray.length - 1] & 0x01) == POINTER_LAST_BIT;
    }

    public boolean isNullPointer() {
        return intValue() == 0;
    }

    public byte[] getBytes() {
        return this.byteArray;
    }

    public char charValue() {
        return (char) intValue();
    }

    public int intValue() {
        return innerRepresentationToIntValue();
    }

    public float floatValue() {
        return innerRepresentationToFloat(this.byteArray);
    }

    public boolean boolValue() {
        return intValue() != 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isPointer()) {
            sb.append("&");
        }
        // We don't know what it is, so even if it's a float write int
        sb.append(intValue());
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StackValue) {
            StackValue value = (StackValue) obj;
            return this.hashCode() == value.hashCode();
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return TypeConverter.byteArrayToInt(this.getBytes());
    }
}
