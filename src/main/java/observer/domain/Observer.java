package observer.domain;

public class Observer {
    private String peerName;
    private String observerWsUrl;
    public Observer() {
        this.peerName = System.getenv().getOrDefault("PEER_NAME", "unknown-peer");
        this.observerWsUrl = System.getenv().getOrDefault(
                "OBSERVER_WS_URL",
                "ws://observer:8080/ws/ingest"
        );
    }

    public String getPeerName() {
        return peerName;
    }

    public String getObserverWsUrl() {
        return observerWsUrl;
    }
}
