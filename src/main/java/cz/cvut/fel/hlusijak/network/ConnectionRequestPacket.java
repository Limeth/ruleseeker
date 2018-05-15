package cz.cvut.fel.hlusijak.network;

/**
 * Direction: Slave -> Master
 *
 * A packet sent directly after establishing a TCP connection with the server.
 */
public class ConnectionRequestPacket implements Packet {
    private final String gitCommitId;

    /**
     * @param gitCommitId The slave's commit ID.
     */
    public ConnectionRequestPacket(String gitCommitId) {
        this.gitCommitId = gitCommitId;
    }

    // Kryonet requires a default constructor for registered classes
    private ConnectionRequestPacket() {
        this(null);
    }

    /**
     * @return The slave's commit ID.
     */
    public String getGitCommitId() {
        return gitCommitId;
    }

    @Override
    public String toString() {
        return "ConnectionRequestPacket{" +
                "gitCommitId='" + gitCommitId + '\'' +
                '}';
    }
}
