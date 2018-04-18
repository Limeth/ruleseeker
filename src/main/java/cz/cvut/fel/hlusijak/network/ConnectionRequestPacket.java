package cz.cvut.fel.hlusijak.network;

public class ConnectionRequestPacket implements Packet {
    private final String gitCommitId;

    public ConnectionRequestPacket(String gitCommitId) {
        this.gitCommitId = gitCommitId;
    }

    // Kryonet requires a default constructor for registered classes
    private ConnectionRequestPacket() {
        this(null);
    }

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
