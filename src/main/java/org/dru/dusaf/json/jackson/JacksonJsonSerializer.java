package org.dru.dusaf.json.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.dru.dusaf.json.Json;
import org.dru.dusaf.json.JsonSerializer;

import java.io.*;

public final class JacksonJsonSerializer implements JsonSerializer {
    private final ObjectMapper mapper;
    private final JsonNodeFactory factory;
    private final JacksonJson nullElem;

    public JacksonJsonSerializer() {
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, false);
        factory = new JsonNodeFactory(true);
        nullElem = new JacksonJson(factory.nullNode(), this);
    }

    @Override
    public Json getNull() {
        return nullElem;
    }

    @Override
    public Json newArray() {
        return getElem(factory.arrayNode());
    }

    @Override
    public Json newObject() {
        return getElem(factory.objectNode());
    }

    @Override
    public Json encode(final Object value) {
        if (value == null) {
            return getNull();
        } else if (value instanceof JacksonJson) {
            return (JacksonJson) value;
        } else if (value instanceof Json) {
            return parse(((Json) value).stringify());
        } else if (value instanceof JsonNode) {
            return getElem((JsonNode) value);
        } else {
            return getElem(mapper.valueToTree(value));
        }
    }

    @Override
    public Json read(final InputStream in) throws IOException {
        return getElem(mapper.readTree(in));
    }

    @Override
    public Json read(final Reader in) throws IOException {
        return getElem(mapper.readTree(in));
    }

    @Override
    public Json parse(final String json) {
        return parseImpl(json);
    }

    JacksonJson getElem(final JsonNode node) {
        if (node == null) {
            return null;
        } else if (node == nullElem.node) {
            return nullElem;
        } else {
            return new JacksonJson(node, this);
        }
    }

    JsonNode getNode(final Json elem) {
        if (elem instanceof JacksonJson) {
            return ((JacksonJson) elem).node;
        } else {
            return parseImpl(elem.stringify()).node;
        }
    }

    <T> T decode(final JacksonJson elem, final Class<T> type) {
        try {
            return mapper.treeToValue(elem.node, type);
        } catch (final IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    void write(final OutputStream out, final JacksonJson elem) throws IOException {
        mapper.writeValue(out, elem.node);
    }

    void write(final Writer out, final JacksonJson elem) throws IOException {
        mapper.writeValue(out, elem.node);
    }

    String stringify(final JacksonJson elem) {
        try {
            return mapper.writeValueAsString(elem.node);
        } catch (final IOException exc) {
            throw new RuntimeException(exc);
        }
    }

    private JacksonJson parseImpl(final String json) {
        try {
            return getElem(mapper.readTree(json));
        } catch (final IOException exc) {
            throw new RuntimeException(exc);
        }
    }
}
