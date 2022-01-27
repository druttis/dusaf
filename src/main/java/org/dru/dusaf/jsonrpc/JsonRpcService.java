package org.dru.dusaf.jsonrpc;

import org.dru.dusaf.json.Json;

import java.io.IOException;

public interface JsonRpcService {
    Json exchange(Json outbound) throws IOException;
}
