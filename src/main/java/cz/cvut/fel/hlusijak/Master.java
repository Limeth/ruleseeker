package cz.cvut.fel.hlusijak;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.google.common.collect.Sets;

import cz.cvut.fel.hlusijak.command.CommandMaster;
import cz.cvut.fel.hlusijak.network.ConnectionRequestPacket;
import cz.cvut.fel.hlusijak.network.ConnectionResultPacket;
import cz.cvut.fel.hlusijak.network.Network;
import cz.cvut.fel.hlusijak.network.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public class Master extends Listener implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Master.class);
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
            LOGGER.error("Could not bind to the address: {}", e.getLocalizedMessage());
            System.exit(2);
        }

        server.start();
    }

    @Override
    public void connected(Connection connection) {
        LOGGER.info("Receiving connection from {}...", connection.getRemoteAddressTCP());
    }

    @Override
    public void received(Connection connection, Object packet) {
        if (!(packet instanceof Packet)) {
            return;
        }

        LOGGER.debug("Received packet from {}: {}", connection.getRemoteAddressTCP(), packet);

        if (approvedConnections.contains(connection)) {

        } else if (packet instanceof ConnectionRequestPacket) {
            LOGGER.debug("Received connection request packet from {}", connection.getRemoteAddressTCP());
            ConnectionRequestPacket crp = (ConnectionRequestPacket) packet;
            ConnectionResultPacket response;
            boolean close;

            if (crp.getGitCommitId().equals(RuleSeeker.getInstance().getGitCommitId())) {
                response = new ConnectionResultPacket(null);
                close = false;

                LOGGER.info("Approved connection request from {}, registering as {}", connection.getRemoteAddressTCP(),
                        connection);
                approvedConnections.add(connection);
            } else {
                response = new ConnectionResultPacket(
                        "Different commit id, are you sure you're up to date with the server version? Server: '"
                                + RuleSeeker.getInstance().getGitCommitId() + "' Client: '" + crp.getGitCommitId()
                                + "'");
                close = true;

                LOGGER.info("Rejected connection request from {}, reason: {}", connection.getRemoteAddressTCP(),
                        response.getError().get());
            }

            connection.sendTCP(response);

            if (close) {
                connection.close();
            }
        } else {
            LOGGER.info("Received unexpected packet from {}, closing connection",
                    connection.getRemoteAddressTCP().toString());
            connection.close();
        }
    }

    @Override
    public void disconnected(Connection connection) {
        if (approvedConnections.remove(connection)) {
            LOGGER.info("Closing connection with {}", connection);
        }
    }
}
