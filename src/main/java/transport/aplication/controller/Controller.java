package transport.aplication.controller;

import synchronization.application.infra.BroadcastController;
import synchronization.application.listener.StrategyMiddleware;
import transport.aplication.service.BroadcastService;

public class Controller implements BroadcastController {
    private final BroadcastService broadcastService;

    public Controller(BroadcastService broadcastService) {
        this.broadcastService = broadcastService;
    }

    @Override
    public void start(StrategyMiddleware strategyMiddleware) {
        broadcastService.setListener(strategyMiddleware);
        broadcastService.start();
    }

    @Override
    public void stop() {
        broadcastService.stop();
    }

    @Override
    public void broadcast(String record) {
        broadcastService.broadcast(record);
    }

    @Override
    public void send(String peerId, String record) {
        broadcastService.send(peerId, record);
    }
}
