package org.dru.dusaf.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface TypeSerializer<T> {
    /**
     * Decodes a non-null value from specified input stream.
     *
     * @param in the input stream to decode the value from.
     * @return the decoded non-null value.
     * @throws IOException on any I/O error.
     */
    T decode(InputStream in) throws IOException;

    /**
     * Returns the length of specified value in bytes.
     *
     * @param value the value of which length in bytes to return.
     * @return the length in bytes of specified value, or -1 if not applicable or letting caller determine length.
     */
    int numBytes(T value);

    /**
     * Encodes specified non-null value to an output stream.
     *
     * @param out   the output stream on which to encode specified non-null value.
     * @param value the non-null value to encode.
     * @throws IOException on any I/O error.
     */
    void encode(OutputStream out, T value) throws IOException;
}
