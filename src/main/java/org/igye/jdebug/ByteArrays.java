package org.igye.jdebug;

import org.igye.jdebug.messages.RepresentableAsArrayOfBytes;

import java.util.ArrayList;
import java.util.List;

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

    public static long byteArrayToLong(byte[] arr, ArrayOffset offset, int length) {
        long res = byteArrayToLong(arr, offset.getOffset(), length);
        offset.increase(length);
        return res;
    }

    public static byte[] byteArrayToByteArray(byte[] arr, int offset, int length) {
        byte[] res = new byte[length];
        System.arraycopy(arr, offset, res, 0, length);
        return res;
    }

    public static byte[] byteArrayToByteArray(byte[] arr, ArrayOffset offset, int length) {
        byte[] res = byteArrayToByteArray(arr, offset.getOffset(), length);
        offset.increase(length);
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

    public static byte[] toByteArray(RepresentableAsArrayOfBytes... elems) {
        if (elems != null) {
            int notNullCnt = 0;
            for (RepresentableAsArrayOfBytes elem : elems) {
                if (elem != null) {
                    notNullCnt++;
                }
            }
            byte[][] arrays = new byte[notNullCnt][];
            int idx = 0;
            for (RepresentableAsArrayOfBytes elem : elems) {
                if (elem != null) {
                    arrays[idx] = elem.toByteArray();
                    idx++;
                }
            }
            return concat(arrays);
        } else {
            return null;
        }
    }
}
