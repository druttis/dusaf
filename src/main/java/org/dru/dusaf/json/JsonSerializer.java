package org.dru.dusaf.json;

import java.io.*;

public interface JsonSerializer {
    Json getNull();

    Json newArray();

    Json newObject();

    Json encode(Object value);

    Json read(InputStream in) throws IOException;

    Json read(Reader in) throws IOException;

    Json parse(String json);
}
