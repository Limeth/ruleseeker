package cz.cvut.fel.hlusijak.network;

import java.util.Optional;

public class ConnectionResultPacket {
    private final String error;

    public ConnectionResultPacket(String error) {
        this.error = error;
    }

    public Optional<String> getError() {
        return Optional.ofNullable(error);
    }
}
