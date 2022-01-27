package org.dru.dusaf.jsonrpc;

import org.dru.dusaf.io.OutputInputStream;
import org.dru.dusaf.json.Json;
import org.dru.dusaf.json.JsonSerializer;
import org.dru.dusaf.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public final class HttpJsonRpcService implements JsonRpcService {
    private final URL url;
    private final JsonSerializer jsonSerializer;

    public HttpJsonRpcService(final String url, final JsonSerializer jsonSerializer) {
        Objects.requireNonNull(url, "url");
        try {
            this.url = new URL(url);
        } catch (final MalformedURLException exc) {
            throw new RuntimeException(exc);
        }
        this.jsonSerializer = jsonSerializer;
    }

    @Override
    public Json exchange(final Json outbound) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        final InputStream temp;
        {
            final OutputInputStream out = new OutputInputStream();
            outbound.write(out);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Content-Length", String.valueOf(out.size()));
            temp = out.getInputStream();
        }
        final Json inbound;
        try (final OutputStream out = connection.getOutputStream()) {
            IOUtils.copy(temp, out);
            try (final InputStream in = connection.getInputStream()) {
                inbound = jsonSerializer.read(in);
            }
        }
        return inbound;
    }
}
