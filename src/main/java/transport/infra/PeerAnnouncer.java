package transport.infra;

import java.util.function.Supplier;

public class PeerAnnouncer {

    private final Supplier<String> messageSupplier;
    private final java.util.function.Consumer<String> sender;
    private final long intervalMillis;

    private volatile boolean running;

    public PeerAnnouncer(Supplier<String> messageSupplier,
                         java.util.function.Consumer<String> sender,
                         long intervalMillis) {
        this.messageSupplier = messageSupplier;
        this.sender = sender;
        this.intervalMillis = intervalMillis;
    }

    public void run() {
        running = true;

        while (running) {
            sender.accept(messageSupplier.get());

            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    public void stop() {
        running = false;
    }
}