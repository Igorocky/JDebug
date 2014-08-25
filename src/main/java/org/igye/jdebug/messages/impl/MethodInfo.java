package org.igye.jdebug.messages.impl;

import org.igye.jdebug.datatypes.impl.MethodId;

public class MethodInfo {
    private MethodId methodId;
    private String Name;
    private String signature;
    private int modBits;

    public MethodInfo(MethodId methodId, String name, String signature, int modBits) {
        this.methodId = methodId;
        Name = name;
        this.signature = signature;
        this.modBits = modBits;
    }

    public MethodId getMethodId() {
        return methodId;
    }

    public String getName() {
        return Name;
    }

    public String getSignature() {
        return signature;
    }

    public int getModBits() {
        return modBits;
    }
}
