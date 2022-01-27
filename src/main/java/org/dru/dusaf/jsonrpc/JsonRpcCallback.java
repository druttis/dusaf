package org.dru.dusaf.jsonrpc;

import org.dru.dusaf.json.Json;

@FunctionalInterface
public interface JsonRpcCallback {
    void accept(Json response);
}
