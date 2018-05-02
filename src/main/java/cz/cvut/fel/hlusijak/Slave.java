package cz.cvut.fel.hlusijak;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.common.base.Preconditions;
import cz.cvut.fel.hlusijak.command.CommandSlave;
import cz.cvut.fel.hlusijak.network.ConnectionRequestPacket;
import cz.cvut.fel.hlusijak.network.MiningRequestPacket;
import cz.cvut.fel.hlusijak.network.MiningResultPacket;
import cz.cvut.fel.hlusijak.network.Network;
import cz.cvut.fel.hlusijak.network.Packet;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class Slave extends Listener implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Slave.class);
    private Miner miner = new Miner(this::onResultFound);
    private Client client;
    private CommandSlave options;

    public Slave(CommandSlave options) {
        this.options = options;
    }

    public void run() {
        validateOptions();

        int port = Optional.ofNullable(options.masterPort).orElse(Network.SERVER_PORT_DEFAULT);
        // TODO: might want to raise this to allow larger grids
        client = new Client(8192, 8192);

        Network.register(client);
        client.addListener(this);
        client.start();

        while (!Thread.interrupted()) {
            LOGGER.info("Connecting as a slave to /{}:{}", options.masterAddress, port);

            try {
                client.connect(Network.CONNECTION_TIMEOUT_MILLIS, options.masterAddress, port);
            } catch (IOException e) {
                LOGGER.warn("Connection attempt failed: {}", e.getLocalizedMessage());
                LOGGER.warn("Retrying in {} seconds", (double) Network.CONNECTION_RETRY_PERIOD / 1000.0);

                try {
                    Thread.sleep(Network.CONNECTION_RETRY_PERIOD);
                } catch (InterruptedException e1) {
                    System.exit(0);
                }
            }

            // Consider running asynchronously via new Thread(client).start()
            while (client.isConnected() && !Thread.interrupted()) {}
        }
    }

    private void validateOptions() {
        try {
            Preconditions.checkNotNull(options.masterAddress, "The master address must be specified.");
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            System.exit(2);
        }
    }

    private void onResultFound(RuleSet result) {
        LOGGER.info("Result found: " + result);
        client.sendTCP(new MiningResultPacket(result));
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

        if (packet instanceof MiningRequestPacket) {
            MiningRequestPacket mrp = (MiningRequestPacket) packet;
            miner.mine(mrp.getSeed(), mrp.getMinIterations(), mrp.getMaxIterations());
        }
    }

    @Override
    public void disconnected(Connection connection) {
        LOGGER.info("Connection closed");
    }
}
