package org.dru.dusaf.json.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.dru.dusaf.json.AbstractJson;
import org.dru.dusaf.json.Json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class JacksonJson extends AbstractJson {
    final JsonNode node;
    private transient final JacksonJsonSerializer serializer;

    JacksonJson(final JsonNode node, final JacksonJsonSerializer serializer) {
        this.node = node;
        this.serializer = serializer;
    }

    @Override
    public boolean isNull() {
        return node.isNull();
    }

    @Override
    public boolean isBoolean() {
        return node.isBoolean();
    }

    @Override
    public boolean isNumber() {
        return node.isNumber();
    }

    @Override
    public boolean isIntegral() {
        return node.isIntegralNumber();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public boolean isByte() {
        try {
            bigIntegerValue().byteValueExact();
            return true;
        } catch (final ArithmeticException exc) {
            return false;
        }
    }

    @Override
    public boolean isShort() {
        return node.isShort();
    }

    @Override
    public boolean isInt() {
        return isIntegral() && node.canConvertToInt();
    }

    @Override
    public boolean isLong() {
        return isIntegral() && node.canConvertToLong();
    }

    @Override
    public boolean isFloat() {
        return node.isFloat();
    }

    @Override
    public boolean isDouble() {
        return node.isDouble();
    }

    @Override
    public boolean isBigInteger() {
        return node.isBigInteger();
    }

    @Override
    public boolean isBigDecimal() {
        return node.isBigDecimal();
    }

    @Override
    public boolean isString() {
        return node.isTextual();
    }

    @Override
    public boolean isArray() {
        return node.isArray();
    }

    @Override
    public boolean isObject() {
        return node.isObject();
    }

    @Override
    public boolean isContainer() {
        return node.isContainerNode();
    }

    @Override
    public boolean booleanValue() {
        return node.booleanValue();
    }

    @Override
    public Number numberValue() {
        return node.numberValue();
    }

    @Override
    public byte byteValue() {
        try {
            return bigIntegerValue().byteValueExact();
        } catch (final ArithmeticException exc) {
            return 0;
        }
    }

    @Override
    public short shortValue() {
        return node.shortValue();
    }

    @Override
    public int intValue() {
        return node.intValue();
    }

    @Override
    public long longValue() {
        return node.longValue();
    }

    @Override
    public float floatValue() {
        return node.floatValue();
    }

    @Override
    public double doubleValue() {
        return node.doubleValue();
    }

    @Override
    public BigInteger bigIntegerValue() {
        return node.bigIntegerValue();
    }

    @Override
    public BigDecimal decimalValue() {
        return node.decimalValue();
    }

    @Override
    public String stringValue() {
        return node.toString();
    }

    @Override
    public <T> T decode(final Class<T> type) {
        return serializer.decode(this, type);
    }

    @Override
    public void write(final OutputStream out) throws IOException {
        serializer.write(out, this);
    }

    @Override
    public void write(final Writer out) throws IOException {
        serializer.write(out, this);
    }

    @Override
    public String stringify() {
        return serializer.stringify(this);
    }

    @Override
    protected int sizeImpl() {
        return node.size();
    }

    @Override
    protected boolean hasImpl(final int index) {
        return node.has(index);
    }

    @Override
    protected Json getImpl(final int index) {
        return getElem(node.get(index));
    }

    @Override
    protected Json encode(final Object value) {
        return serializer.encode(value);
    }

    @Override
    protected void addImpl(final int index, final Json elem) {
        getArrayNode().insert(index, getNode(elem));
    }

    @Override
    protected Json setImpl(final int index, final Json elem) {
        return getElem(getArrayNode().set(index, getNode(elem)));
    }

    @Override
    protected Json removeImpl(final int index) {
        return getElem(getArrayNode().remove(index));
    }

    @Override
    protected Set<String> idsImpl() {
        final Set<String> ids = new HashSet<>();
        node.fieldNames().forEachRemaining(ids::add);
        return Collections.unmodifiableSet(ids);
    }

    @Override
    protected boolean hasImpl(final String name) {
        return node.has(name);
    }

    @Override
    protected Json getImpl(final String name) {
        return getElem(node.get(name));
    }

    @Override
    protected Json putImpl(final String name, final Json elem) {
        return getElem(getObjectNode().replace(name, getNode(elem)));
    }

    @Override
    protected Json removeImpl(final String name) {
        return getElem(getObjectNode().remove(name));
    }

    @Override
    protected void clearImpl() {
        getContainerNode().removeAll();
    }

    private Json getElem(final JsonNode node) {
        return serializer.getElem(node);
    }

    private ArrayNode getArrayNode() {
        return (ArrayNode) node;
    }

    private JsonNode getNode(final Json elem) {
        return serializer.getNode(elem);
    }

    private ObjectNode getObjectNode() {
        return (ObjectNode) node;
    }

    private ContainerNode<?> getContainerNode() {
        return (ContainerNode<?>) node;
    }
}
