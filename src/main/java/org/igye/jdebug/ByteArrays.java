package org.igye.jdebug;


public class ByteArrays {
    public static byte[] intToBigEndianByteArray(int intVal) {
        return new byte[]{
                (byte) (intVal >>> 24),
                (byte) (intVal >>> 16),
                (byte) (intVal >>> 8),
                (byte) intVal
        };
    }

    public static long fourByteArrayToLong(byte[] arr) {
        if (arr.length != 4) {
            throw new IllegalArgumentException("arr.length != 4");
        }
        long res = 0;
        for (byte b : arr) {
            res = (res << 8) + (b & 0xff);
        }
        return res;
    }

    public static byte[] concat(byte[] arr1, byte[] arr2) {
        byte[] res = new byte[arr1.length + arr2.length];
        System.arraycopy(arr1, 0, res, 0, arr1.length);
        System.arraycopy(arr2, 0, res, arr1.length, arr2.length);
        return res;
    }
}
