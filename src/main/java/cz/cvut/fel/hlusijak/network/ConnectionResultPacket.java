package cz.cvut.fel.hlusijak.network;

import java.util.Optional;

/**
 * Direction: Master -> Slave
 *
 * A packet sent as a response to {@link ConnectionRequestPacket}. May signal
 * that either the connection has been accepted, or rejected with a specific
 * {@link #error}.
 */
public class ConnectionResultPacket implements Packet {
    private final String error;

    /**
     * @param error The error that occurred, signalling the connection request
     *              was rejected, or {@code null} if successful.
     */
    public ConnectionResultPacket(String error) {
        this.error = error;
    }

    // Kryonet requires a default constructor for registered classes
    private ConnectionResultPacket() {
        this(null);
    }

    /**
     * @return The error that occurred, signalling the connection request was
     *         rejected, or {@link Optional#empty()} if successful.
     */
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
