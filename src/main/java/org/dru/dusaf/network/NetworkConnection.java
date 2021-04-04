package org.dru.dusaf.network;

import java.nio.ByteBuffer;

public interface NetworkConnection extends NetworkObject {
    void read(ByteBuffer target);

    void write(ByteBuffer source);
}
