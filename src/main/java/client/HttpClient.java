package client;

import com.google.common.collect.ImmutableMap;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import java.net.URI;
import java.util.Map;

public class HttpClient {

    private final URI baseUri;

    public HttpClient(String baseUri) {
        this.baseUri = URI.create(baseUri);
    }

    public HttpResponse get(String path) {
        return execute("GET", path);
    }

    public HttpResponse execute(String method, String path) {
        return execute(method, ImmutableMap.<String, String>of(), path);
    }

    public HttpResponse execute(String method, Map<String, String> headers, String path) {
        HttpRequest request = new HttpRequest().method(method).set(baseUri.resolve(path).toASCIIString());
        headers.entrySet().stream().forEach(entry ->
                        request.header(entry.getKey(), entry.getValue())
        );
        return request.send();
    }
}
