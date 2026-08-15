package observer.infra;

import observer.domain.Observer;
import observer.domain.WsEvent;
import synchronization.application.listener.DummyDTO;
import synchronization.application.listener.StrategyDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WsInfraSenderImpl implements WsInfraSender {
    private final Queue<String> pendingMessages;
    private WebSocket webSocket;
    private boolean connected;
    private Observer observer;

    public WsInfraSenderImpl(Observer observer) {
        this.pendingMessages = new ConcurrentLinkedQueue<>();
        this.observer = observer;
    }

    @Override
    public void connect() {
        HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(observer.getObserverWsUrl()), this)
                .thenAccept(socket -> {
                    this.webSocket = socket;
                    this.connected = true;

                    publish(WsEvent.CONNECTED, new DummyDTO());

                    flushPendingMessages();
                })
                .exceptionally(error -> {
                    System.err.println("Could not connect to observer: " + error.getMessage());
                    return null;
                });
    }

    @Override
    public void publish(WsEvent wsEvent, StrategyDTO dto) {
        try {

            String message = """
                {
                  "time": %s,
                  "peer": %s,
                  "event": %s,
                  "payload": %s
                }
                """.formatted(
                    jsonString(Instant.now().toString()),
                    jsonString(observer.getPeerName()),
                    jsonString(wsEvent.name()),
                    jsonString(dto.toString())
            );

            if (!connected || webSocket == null) {
                pendingMessages.add(message);
                return;
            }

            webSocket.sendText(message, true);
        } catch (Exception exception) {
            System.err.println("Could not publish observer event: " + exception.getMessage());
        }
    }

    private void flushPendingMessages() {
        String message;

        while ((message = pendingMessages.poll()) != null) {
            webSocket.sendText(message, true);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(
            WebSocket webSocket,
            CharSequence data,
            boolean last
    ) {
        webSocket.request(1);
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        this.connected = false;
        System.err.println("Observer WebSocket error: " + error.getMessage());
    }

    @Override
    public CompletionStage<?> onClose(
            WebSocket webSocket,
            int statusCode,
            String reason
    ) {
        this.connected = false;
        System.out.println("Observer WebSocket closed: " + statusCode + " " + reason);
        return null;
    }

    private static String jsonString(String value) {
        if (value == null) {
            return "null";
        }

        return "\"" + escapeJson(value) + "\"";
    }

    private static String escapeJson(String value) {
        StringBuilder escaped = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);

            switch (character) {
                case '"' -> escaped.append("\\\"");
                case '\\' -> escaped.append("\\\\");
                case '\b' -> escaped.append("\\b");
                case '\f' -> escaped.append("\\f");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }

        return escaped.toString();
    }
}
