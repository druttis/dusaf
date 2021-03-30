package org.dru.dusaf.util;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Streamable<E> extends Iterable<E> {
    default Stream<E> stream(boolean parallel) {
        return StreamSupport.stream(spliterator(), parallel);
    }

    default Stream<E> stream() {
        return stream(false);
    }
}
