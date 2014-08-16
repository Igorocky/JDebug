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
        return byteArrayToLong(arr, 0, 4);
    }

    public static long byteArrayToLong(byte[] arr, int offset, int length) {
        long res = 0;
        for (int i = 0; i < length; i++) {
            res = (res << 8) + (arr[offset + i] & 0xff);
        }
        return res;
    }

    public static int fourByteArrayToInt(byte[] arr) {
        return (int) fourByteArrayToLong(arr);
    }

    public static byte[] concat(byte[]... arrays) {
        byte[] res = new byte[totalLength(arrays)];
        int startIndexInTarget = 0;
        for (byte[] arr : arrays) {
            if (arr != null) {
                System.arraycopy(arr, 0, res, startIndexInTarget, arr.length);
                startIndexInTarget += arr.length;
            }
        }
        return res;
    }

    private static int totalLength(byte[]... arrays) {
        int res = 0;
        for (byte[] arr : arrays) {
            if (arr != null) {
                res += arr.length;
            }
        }
        return res;
    }
}
