package org.igye.jdebug;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ByteArrayTest {
    @Test
    public void testConcat() {
        byte[] arr1 = new byte[] {1,2,3};
        byte[] arr2 = new byte[] {4,5,6};
        byte[] res = ByteArrays.concat(arr1, arr2);
        assertEquals(arr1.length + arr2.length, res.length);
        for (int i = 0; i < arr1.length; i++) {
            assertEquals(arr1[i], res[i]);
        }
        for (int i = 0; i < arr2.length; i++) {
            assertEquals(arr2[i], res[arr1.length + i]);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFourByteArrayToLongWith3ElemArray() {
        ByteArrays.fourByteArrayToLong(new byte[]{1,2,3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFourByteArrayToLongWith5ElemArray() {
        ByteArrays.fourByteArrayToLong(new byte[]{1,2,3,4,5});
    }

    @Test
    public void testFourByteArrayToLong() {
        assertEquals(16909060L, ByteArrays.fourByteArrayToLong(new byte[]{1,2,3,4}));
        assertEquals(4294967295L, ByteArrays.fourByteArrayToLong(new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255}));
    }

    @Test
    public void testFourByteArrayToInt() {
        assertEquals(
                0b00000001_00000001_00000001_00000001,
                ByteArrays.fourByteArrayToInt(new byte[]{0b00000001, 0b00000001, 0b00000001, 0b00000001})
        );
        assertEquals(
                -1,
                ByteArrays.fourByteArrayToInt(new byte[]{(byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111, (byte) 0b11111111})
        );
    }

    @Test
    public void testIntToBigEndianByteArray() {
        byte[] res = ByteArrays.intToBigEndianByteArray(16909060);
        assertEquals(res.length, 4);
        assertEquals(res[0], 1);
        assertEquals(res[1], 2);
        assertEquals(res[2], 3);
        assertEquals(res[3], 4);
    }
}
