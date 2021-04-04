package org.dru.dusaf.network;

import java.nio.ByteBuffer;

public enum NetworkProtocols implements NetworkProtocol {
    DEFAULT {
        @Override
        public ByteBuffer decode(final ByteBuffer buffer) {
            return buffer;
        }

        @Override
        public ByteBuffer encode(final ByteBuffer buffer) {
            return buffer;
        }
    }
}
