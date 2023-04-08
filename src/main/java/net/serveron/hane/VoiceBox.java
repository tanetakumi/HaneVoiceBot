package net.serveron.hane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class VoiceBox {
    private static final HttpClient client;
    private static final String address;

    static {
        client = HttpClient.newBuilder().build();
        address = "192.168.1.151:50021";
    }

    public static CompletableFuture<byte[]> getVoice(String text){
        HttpRequest req1 = HttpRequest.newBuilder()
                .uri(URI.create("http://"+address+"/audio_query?speaker=1&text="+text))
                .version(HttpClient.Version.HTTP_1_1)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return client.sendAsync(req1, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)).thenApplyAsync(res -> {
            if (res.statusCode() < 200 || res.statusCode() >= 300) {
                throw new UncheckedIOException(new IOException(String.format(Locale.ROOT, "Page returned code %d", res.statusCode())));
            }
            return res.body();

        }).thenCompose(json -> {
            HttpRequest req2 = HttpRequest.newBuilder()
                    .header("Content-Type", "application/json")
                    .uri(URI.create("http://"+address+"/synthesis?speaker=1"))
                    .version(HttpClient.Version.HTTP_1_1)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            return client.sendAsync(req2, HttpResponse.BodyHandlers.ofByteArray()).thenApplyAsync(res -> {
                if (res.statusCode() < 200 || res.statusCode() >= 300) {
                    throw new UncheckedIOException(new IOException(String.format(Locale.ROOT, "Page returned code %d", res.statusCode())));
                }
                return res.body();
            });
        });
    }
}

