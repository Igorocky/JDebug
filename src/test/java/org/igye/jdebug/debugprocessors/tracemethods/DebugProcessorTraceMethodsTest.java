package org.igye.jdebug.debugprocessors.tracemethods;

import org.junit.Assert;
import org.junit.Test;

public class DebugProcessorTraceMethodsTest {

    @Test
    public void convertClassNameFromJniToNormal() {
        DebugProcessorTraceMethods proc = new DebugProcessorTraceMethods();
        Assert.assertEquals(
                proc.convertClassNameFromJniToNormal("Lorg/igye/simpledebugee/Class2;"),
                "org.igye.simpledebugee.Class2"
        );
    }
}
