package org.dru.dusaf.jsonrpc;

import org.dru.dusaf.json.Json;
import org.dru.dusaf.json.JsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public final class JsonRpcClient {
    private static final Logger logger = LoggerFactory.getLogger(JsonRpcClient.class);
    private static final Executor executor = Executors.newFixedThreadPool(2);

    private final JsonRpcService service;
    private final JsonSerializer jsonSerializer;
    private final Object monitor;
    private JsonRpcBatch batch;

    JsonRpcClient(final JsonRpcService service, final JsonSerializer jsonSerializer) {
        this.service = service;
        this.jsonSerializer = jsonSerializer;
        monitor = new Object();
        batch = new JsonRpcBatch(jsonSerializer);
    }

    public void request(final String method, final Object params, final JsonRpcCallback callback) {
        Objects.requireNonNull(callback, "callback");
        enqueue(method, params, callback);
    }

    public void notify(final String method, final Object params) {
        final Json message = createMessage(method, params);
        enqueue(method, params, null);
    }

    public void flush() {
        final JsonRpcBatch outbound;
        synchronized (monitor) {
            if (batch.isEmpty()) {
                return;
            }
            outbound = batch;
            batch = new JsonRpcBatch(jsonSerializer);
        }
        outbound.exchange(service);
    }

    private Json createMessage(final String method, final Object params) {
        Objects.requireNonNull(method, "method");
        final Json message = jsonSerializer.newObject();
        message.put("jsonrpc", "2.0");
        message.put("method", method);
        if (params != null) {
            message.put("params", jsonSerializer.encode(params));
        }
        return message;
    }

    private void enqueue(final String method, final Object params, final JsonRpcCallback callback) {
        final Json message = createMessage(method, params);
        synchronized (monitor) {
            if (batch.enqueue(message, callback)) {
                executor.execute(this::flush);
            }
        }
    }
}
