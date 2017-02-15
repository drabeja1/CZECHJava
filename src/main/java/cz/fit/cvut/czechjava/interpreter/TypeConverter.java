package cz.fit.cvut.czechjava.interpreter;

import cz.fit.cvut.czechjava.interpreter.memory.Array;
import cz.fit.cvut.czechjava.type.FloatType;
import cz.fit.cvut.czechjava.type.NumberType;

import java.nio.ByteBuffer;

/**
 * Converter between data types
 * 
 * @author Jakub
 */
public class TypeConverter {

    public static float byteArrayToFloat(byte[] bytes, int from) {
        return ByteBuffer.wrap(bytes, from, NumberType.SIZE).getFloat();
    }

    public static float byteArrayToFloat(byte[] bytes) {
        return byteArrayToFloat(bytes, 0);
    }

    public static int byteArrayToInt(byte[] bytes) {
        return byteArrayToInt(bytes, 0);
    }

    public static int byteArrayToInt(byte[] bytes, int from) {
        return ByteBuffer.wrap(bytes, from, NumberType.SIZE).getInt();
    }

    public static float stringToFloat(String floatString) {
        return Float.parseFloat(floatString);
    }

    public static float intToFloat(int i) {
        return (float) i;
    }

    public static int floatToInt(float f) {
        return (int) f;
    }

    public static byte[] intToByteArray(int i) {
        return ByteBuffer.allocate(NumberType.SIZE).putInt(i).array();
    }

    public static byte[] floatToByteArray(float i) {
        return ByteBuffer.allocate(FloatType.SIZE).putFloat(i).array();
    }

    public static char[] arrayToCharArray(Array array) {
        char[] chars = new char[array.getSize()];
        for (int i = 0; i < array.getSize(); i++) {
            chars[i] = array.get(i).charValue();
        }
        return chars;
    }

    public static Array charArrayToArray(char[] charArray) {
        Array array = new Array(charArray.length);
        for (int i = 0; i < charArray.length; i++) {
            StackValue charValue = new StackValue(charArray[i], StackValue.Type.Primitive);
            array.set(i, charValue);
        }
        return array;
    }

    public static boolean[] byteArrayToBoolArray(byte[] bytes) {
        int[] intArray = byteArrayToIntArray(bytes);
        boolean[] res = new boolean[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            res[i] = (intArray[i] != 0);
        }
        return res;
    }

    public static int[] byteArrayToIntArray(byte[] bytes) {
        int intArrayLength = bytes.length / NumberType.SIZE;
        int[] res = new int[intArrayLength];
        for (int i = 0; i < intArrayLength; i++) {
            res[i] = byteArrayToInt(bytes, i * NumberType.SIZE);
        }
        return res;
    }

    public static float[] byteArrayToFloatArray(byte[] bytes) {
        int floatArrayLength = bytes.length / FloatType.SIZE;
        float[] res = new float[floatArrayLength];
        for (int i = 0; i < floatArrayLength; i++) {
            res[i] = byteArrayToFloat(bytes, i * FloatType.SIZE);
        }
        return res;
    }

}
