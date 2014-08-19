package org.igye.jdebug.messages.core;

public class IdSizes {
    private static int fieldIDSize;
    private static int methodIDSize;
    private static int objectIDSize;
    private static int referenceTypeIDSize;
    private static int frameIDSize;

    public static int getFieldIDSize() {
        return fieldIDSize;
    }

    public static void setFieldIDSize(int fieldIDSize) {
        IdSizes.fieldIDSize = fieldIDSize;
    }

    public static int getMethodIDSize() {
        return methodIDSize;
    }

    public static void setMethodIDSize(int methodIDSize) {
        IdSizes.methodIDSize = methodIDSize;
    }

    public static int getObjectIDSize() {
        return objectIDSize;
    }

    public static void setObjectIDSize(int objectIDSize) {
        IdSizes.objectIDSize = objectIDSize;
    }

    public static int getReferenceTypeIDSize() {
        return referenceTypeIDSize;
    }

    public static void setReferenceTypeIDSize(int referenceTypeIDSize) {
        IdSizes.referenceTypeIDSize = referenceTypeIDSize;
    }

    public static int getFrameIDSize() {
        return frameIDSize;
    }

    public static void setFrameIDSize(int frameIDSize) {
        IdSizes.frameIDSize = frameIDSize;
    }
}
