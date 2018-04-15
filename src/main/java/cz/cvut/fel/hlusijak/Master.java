package cz.cvut.fel.hlusijak;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.google.common.collect.Sets;
import cz.cvut.fel.hlusijak.command.CommandMaster;
import cz.cvut.fel.hlusijak.network.ConnectionRequestPacket;
import cz.cvut.fel.hlusijak.network.ConnectionResultPacket;
import cz.cvut.fel.hlusijak.network.Network;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class Master extends Listener implements Runnable {
    private CommandMaster options;
    private Set<Connection> approvedConnections = Sets.newHashSet();

    public Master(CommandMaster options) {
        this.options = options;
    }

    public void run() {
        Server server = new Server();

        Network.register(server);
        server.addListener(this);

        try {
            server.bind(Optional.ofNullable(options.port).orElse(Network.SERVER_PORT_DEFAULT));
        } catch (IOException e) {
            System.err.printf("Could not bind to the address: %s\n", e.getLocalizedMessage());
            return;
        }

        server.start();
    }

    @Override
    public void received(Connection connection, Object packet) {
        System.out.println(connection.getRemoteAddressTCP());
        System.out.printf("Received: ");
        System.out.println(packet);

        if (approvedConnections.contains(connection)) {

        } else if (packet instanceof ConnectionRequestPacket) {
            ConnectionRequestPacket crp = (ConnectionRequestPacket) packet;
            ConnectionResultPacket response;
            boolean close;

            if (crp.getGitCommitId().equals(RuleSeeker.getInstance().getGitCommitId())) {
                response = new ConnectionResultPacket(null);
                close = true;
            } else {
                response = new ConnectionResultPacket("Different commit id, are you sure you're up to date with the server version? Server: '" + RuleSeeker.getInstance().getGitCommitId() + "' Client: '" + crp.getGitCommitId() + "'");
                close = false;
            }

            connection.sendTCP(response);

            if (close) {
                connection.close();
            }
        } else {
            connection.close();
        }
    }

    @Override
    public void connected(Connection connection) {
        System.out.printf("Connected: ");
        System.out.println(connection.getRemoteAddressTCP());
    }

    @Override
    public void idle(Connection connection) {
        if (!approvedConnections.contains(connection)) {
            connection.close();
        }
    }

    @Override
    public void disconnected(Connection connection) {
        approvedConnections.remove(connection);

        System.out.printf("Disconnected: ");
        System.out.println(connection.getRemoteAddressTCP());
    }
}
