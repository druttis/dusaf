package org.dru.dusaf.jsonrpc;

import org.dru.dusaf.json.Json;
import org.dru.dusaf.json.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

final class JsonRpcBatch {
    private final JsonSerializer jsonSerializer;
    private final Json outbound;
    private final Map<Long, JsonRpcCallback> callbacks;

    JsonRpcBatch(final JsonSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
        outbound = jsonSerializer.newArray();
        callbacks = new HashMap<>();
    }

    boolean isEmpty() {
        return outbound.size() == 0;
    }

    boolean enqueue(final Json message, final JsonRpcCallback callback) {
        if (callback != null) {
            final long id = outbound.size();
            callbacks.put(id, callback);
            message.put("id", jsonSerializer.encode(id));
        }
        outbound.add(message);
        return outbound.size() == 1;
    }

    void exchange(final JsonRpcService service) {
        final Json inbound;
        try {
            inbound = service.exchange(outbound.size() == 1 ? outbound.get(0) : outbound);
        } catch (final Exception exc) {
            exc.printStackTrace();
            return;
        }
        try {
            receive(inbound);
        } catch (final Exception exc) {
            exc.printStackTrace();
        }
        final Json response = createMessage();
        response.put("error", createError(-1, "forgotten callback"));
        callbacks.values().forEach(callback -> callback.accept(response));
    }

    private void receive(final Json inbound) {
        if (inbound.isArray()) {
            for (int index = 0; index < inbound.size(); index++) {
                accept(inbound.get(index));
            }
        } else {
            accept(inbound);
        }
    }

    private void accept(final Json response) {
        final Json jsonrpc = response.get("jsonrpc");
        if (jsonrpc == null || !jsonrpc.isString()) {
            return;
        }
        final Json id = response.get("id");
        System.out.println("id: " + id.isLong());
        if (id == null || !id.isLong()) {
            System.out.println("id is not a long");
            return;
        }
        final JsonRpcCallback callback = callbacks.remove(id.longValue());
        if (callback != null) {
            callback.accept(response);
        }
    }

    private Json createMessage() {
        final Json message = jsonSerializer.newObject();
        message.put("jsonrpc", "2.0");
        return message;
    }

    private Json createError(final int code, final String message) {
        final Json error = jsonSerializer.newObject();
        error.put("code", code);
        error.put("message", message);
        return error;
    }

    private static final class JsonRpcCallbackWrapper implements JsonRpcCallback {
        private final JsonRpcCallback callback;

        private JsonRpcCallbackWrapper(final JsonRpcCallback callback) {
            this.callback = callback;
        }

        @Override
        public void accept(final Json response) {
            try {
                callback.accept(response);
            } catch (final Exception exc) {
                System.err.println("unhandled exception caught:");
                exc.printStackTrace();
            }
        }
    }
}
