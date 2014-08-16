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

    public static int fourByteArrayToInt(byte[] arr) {
        return (int) fourByteArrayToLong(arr);
    }

//    public static byte[] concat(byte[] arr1, byte[] arr2) {
//        if (arr1 != null && arr2 != null) {
//            byte[] res = new byte[arr1.length + arr2.length];
//            System.arraycopy(arr1, 0, res, 0, arr1.length);
//            System.arraycopy(arr2, 0, res, arr1.length, arr2.length);
//            return res;
//        } else if (arr1 == null && arr2 == null) {
//            return null;
//        } else if (arr1 == null) {
//            byte[] res = new byte[arr2.length];
//            System.arraycopy(arr2, 0, res, 0, arr2.length);
//            return res;
//        } else {
//            byte[] res = new byte[arr1.length];
//            System.arraycopy(arr1, 0, res, 0, arr1.length);
//            return res;
//        }
//    }
//
//    public static byte[] concat(byte[] arr1, byte[] arr2, byte[] arr3, byte[]... arrays) {
//        byte[] res = concat(arr1, arr2);
//        res = concat(res, arr3);
//        for (int i = 0; i < arrays.length; i++) {
//            res = concat(res, arrays[i]);
//        }
//        return res;
//    }

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
