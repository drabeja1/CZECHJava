package cz.fit.cvut.czechjava.interpreter;

/**
 *
 * @author Jakub
 */
public abstract class ByteArrayWrapper {

    protected byte[] byteArray;

    public final void setBytes(int from, byte[] bytes) {
        System.arraycopy(bytes, 0, byteArray, from, bytes.length);
    }

    public byte[] getBytes(int from) {
        byte[] bytes = new byte[StackValue.size];

        for (int i = 0; i < StackValue.size; i++) {
            bytes[i] = byteArray[i + from];
        }

        return bytes;
    }
}
