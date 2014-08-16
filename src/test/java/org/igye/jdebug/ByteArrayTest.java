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
}
