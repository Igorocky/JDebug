package org.igye.jdebug.messages.impl;

import org.igye.jdebug.datatypes.impl.FrameId;
import org.igye.jdebug.datatypes.impl.Location;
import org.igye.jdebug.datatypes.impl.MethodId;

public class FrameInfo {
    private FrameId frameId;
    private Location location;

    public FrameInfo(FrameId frameId, Location location) {
        this.frameId = frameId;
        this.location = location;
    }

    public FrameId getFrameId() {
        return frameId;
    }

    public Location getLocation() {
        return location;
    }
}
