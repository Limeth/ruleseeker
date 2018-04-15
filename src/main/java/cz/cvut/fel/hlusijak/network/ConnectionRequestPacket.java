package cz.cvut.fel.hlusijak.network;

public class ConnectionRequestPacket {
    private final String gitCommitId;

    public ConnectionRequestPacket(String gitCommitId) {
        this.gitCommitId = gitCommitId;
    }

    public String getGitCommitId() {
        return gitCommitId;
    }
}
