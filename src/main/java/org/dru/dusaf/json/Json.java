package org.dru.dusaf.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

public interface Json {
    boolean isNull();

    boolean isBoolean();

    boolean isNumber();

    boolean isIntegral();

    boolean isByte();

    boolean isShort();

    boolean isInt();

    boolean isLong();

    boolean isFloat();

    boolean isDouble();

    boolean isBigInteger();

    boolean isBigDecimal();

    boolean isString();

    boolean isArray();

    boolean isObject();

    boolean isContainer();

    boolean booleanValue();

    Number numberValue();

    byte byteValue();

    short shortValue();

    int intValue();

    long longValue();

    float floatValue();

    double doubleValue();

    BigInteger bigIntegerValue();

    BigDecimal decimalValue();

    String stringValue();

    int size();

    boolean has(int index);

    Json get(int index);

    void add(int index, Object value);

    void add(Object value);

    Json set(int index, Object value);

    Json remove(int index);

    Set<String> ids();

    boolean has(String name);

    Json get(String name);

    Json put(String name, Object value);

    Json remove(String name);

    void clear();

    <T> T decode(Class<T> type);

    void write(OutputStream out) throws IOException;

    void write(Writer out) throws IOException;

    String stringify();
}
