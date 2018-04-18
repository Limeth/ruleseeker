package cz.cvut.fel.hlusijak.network;

import java.util.Optional;

public class ConnectionResultPacket implements Packet {
    private final String error;

    public ConnectionResultPacket(String error) {
        this.error = error;
    }

    // Kryonet requires a default constructor for registered classes
    private ConnectionResultPacket() {
        this(null);
    }

    public Optional<String> getError() {
        return Optional.ofNullable(error);
    }

    @Override
    public String toString() {
        return "ConnectionResultPacket{" +
                "error='" + error + '\'' +
                '}';
    }
}
