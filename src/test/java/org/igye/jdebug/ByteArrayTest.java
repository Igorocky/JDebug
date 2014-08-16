package org.igye.jdebug;

import org.junit.Test;

import static org.junit.Assert.*;

public class ByteArrayTest {
    @Test
    public void concat() {
        byte[] arr1 = new byte[] {1,2,3};
        byte[] arr2 = new byte[] {4,5,6,7};

        byte[] res = ByteArrays.concat(arr1, arr2);
        assertEquals(arr1.length + arr2.length, res.length);
        for (int i = 0; i < arr1.length; i++) {
            assertEquals(arr1[i], res[i]);
        }
        for (int i = 0; i < arr2.length; i++) {
            assertEquals(arr2[i], res[arr1.length + i]);
        }

        res = ByteArrays.concat(null, null);
        assertEquals(0, res.length);

        res = ByteArrays.concat(arr1, null);
        assertTrue(res != arr1);
        assertArrayEquals(res, arr1);

        res = ByteArrays.concat(null, arr2);
        assertTrue(res != arr2);
        assertArrayEquals(res, arr2);

        res = ByteArrays.concat(arr1, arr2, arr1);
        assertEquals(res.length, arr1.length*2 + arr2.length);
        for (int i = 0; i < arr1.length; i++) {
            assertEquals(arr1[i], res[i]);
        }
        for (int i = 0; i < arr2.length; i++) {
            assertEquals(arr2[i], res[arr1.length + i]);
        }
        for (int i = 0; i < arr1.length; i++) {
            assertEquals(arr1[i], res[arr1.length + arr2.length + i]);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void fourByteArrayToLongWith3ElemArray() {
        ByteArrays.fourByteArrayToLong(new byte[]{1,2,3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void fourByteArrayToLongWith5ElemArray() {
        ByteArrays.fourByteArrayToLong(new byte[]{1,2,3,4,5});
    }

    @Test
    public void fourByteArrayToLong() {
        assertEquals(16909060L, ByteArrays.fourByteArrayToLong(new byte[]{1,2,3,4}));
        assertEquals(4294967295L, ByteArrays.fourByteArrayToLong(new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255}));
    }

    @Test
    public void fourByteArrayToInt() {
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
    public void intToBigEndianByteArray() {
        byte[] res = ByteArrays.intToBigEndianByteArray(16909060);
        assertEquals(res.length, 4);
        assertEquals(res[0], 1);
        assertEquals(res[1], 2);
        assertEquals(res[2], 3);
        assertEquals(res[3], 4);
    }
}
