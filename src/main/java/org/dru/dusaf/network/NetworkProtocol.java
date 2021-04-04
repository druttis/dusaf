package org.dru.dusaf.network;

import java.nio.ByteBuffer;

public interface NetworkProtocol {
    ByteBuffer decode(ByteBuffer buffer);

    ByteBuffer encode(ByteBuffer buffer);
}
