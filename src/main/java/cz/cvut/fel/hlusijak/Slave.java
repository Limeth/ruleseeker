package cz.cvut.fel.hlusijak;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.common.base.Preconditions;
import cz.cvut.fel.hlusijak.command.CommandSlave;
import cz.cvut.fel.hlusijak.network.ConnectionRequestPacket;
import cz.cvut.fel.hlusijak.network.Network;
import cz.cvut.fel.hlusijak.network.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class Slave extends Listener implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Slave.class);
    private CommandSlave options;

    public Slave(CommandSlave options) {
        this.options = options;
    }

    public void run() {
        validateOptions();

        Client client = new Client();

        Network.register(client);
        client.addListener(this);
        client.start();

        try {
            client.connect(Network.CONNECTION_TIMEOUT_MILLIS, options.masterAddress, Optional.ofNullable(options.masterPort).orElse(Network.SERVER_PORT_DEFAULT));
        } catch (IOException e) {
            LOGGER.error("Could not connect to the server: {}", e.getLocalizedMessage());
            System.exit(2);
        }

        // Consider running asynchronously via new Thread(client).start()
        while (true) {}
    }

    private void validateOptions() {
        try {
            Preconditions.checkNotNull(options.masterAddress, "The master address must be specified.");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            System.exit(2);
        }
    }

    @Override
    public void connected(Connection connection) {
        LOGGER.info("Sending connection request packet to {}", connection.getRemoteAddressTCP());
        connection.sendTCP(new ConnectionRequestPacket(RuleSeeker.getInstance().getGitCommitId()));
    }

    @Override
    public void received(Connection connection, Object packet) {
        if (!(packet instanceof Packet)) {
            return;
        }

        LOGGER.debug("Received packet from {}: {}", connection.getRemoteAddressTCP(), packet);
    }

    @Override
    public void disconnected(Connection connection) {
        LOGGER.info("Connection closed");
    }
}
