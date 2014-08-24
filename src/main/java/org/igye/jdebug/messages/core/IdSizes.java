package org.igye.jdebug.messages.core;

public class IdSizes {
    private static int fieldIDSize;
    private static boolean fieldIDSizeInitialized;
    private static int methodIDSize;
    private static boolean methodIDSizeInitialized;
    private static int objectIDSize;
    private static boolean objectIDSizeInitialized;
    private static int referenceTypeIDSize;
    private static boolean referenceTypeIDSizeInitialized;
    private static int frameIDSize;
    private static boolean frameIDSizeInitialized;

    public static int getFieldIDSize() {
        if (!fieldIDSizeInitialized) {
            new IllegalStateException("!fieldIDSizeInitialized");
        }
        return fieldIDSize;
    }

    public static void setFieldIDSize(int fieldIDSize) {
        IdSizes.fieldIDSize = fieldIDSize;
        fieldIDSizeInitialized = true;
    }

    public static int getMethodIDSize() {
        if (!methodIDSizeInitialized) {
            new IllegalStateException("!methodIDSizeInitialized");
        }
        return methodIDSize;
    }

    public static void setMethodIDSize(int methodIDSize) {
        IdSizes.methodIDSize = methodIDSize;
        methodIDSizeInitialized = true;
    }

    public static int getObjectIDSize() {
        if (!objectIDSizeInitialized) {
            new IllegalStateException("!objectIDSizeInitialized");
        }
        return objectIDSize;
    }

    public static void setObjectIDSize(int objectIDSize) {
        IdSizes.objectIDSize = objectIDSize;
        objectIDSizeInitialized = true;
    }

    public static int getReferenceTypeIDSize() {
        if (!referenceTypeIDSizeInitialized) {
            new IllegalStateException("!referenceTypeIDSizeInitialized");
        }
        return referenceTypeIDSize;
    }

    public static void setReferenceTypeIDSize(int referenceTypeIDSize) {
        IdSizes.referenceTypeIDSize = referenceTypeIDSize;
        referenceTypeIDSizeInitialized = true;
    }

    public static int getFrameIDSize() {
        if (!frameIDSizeInitialized) {
            new IllegalStateException("!frameIDSizeInitialized");
        }
        return frameIDSize;
    }

    public static void setFrameIDSize(int frameIDSize) {
        IdSizes.frameIDSize = frameIDSize;
        frameIDSizeInitialized = true;
    }
}
