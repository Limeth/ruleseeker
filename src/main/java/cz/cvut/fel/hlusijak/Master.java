package cz.cvut.fel.hlusijak;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import cz.cvut.fel.hlusijak.command.CommandMaster;
import cz.cvut.fel.hlusijak.network.ConnectionRequestPacket;
import cz.cvut.fel.hlusijak.network.ConnectionResultPacket;
import cz.cvut.fel.hlusijak.network.MiningRequestPacket;
import cz.cvut.fel.hlusijak.network.MiningResultPacket;
import cz.cvut.fel.hlusijak.network.Network;
import cz.cvut.fel.hlusijak.network.Packet;
import cz.cvut.fel.hlusijak.network.SeedGridChunkPacket;
import cz.cvut.fel.hlusijak.simulator.Simulator;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The server component of the cellular automaton space miner.
 * Communicates with {@link Slave}s, dispatches initial configurations to them,
 * aggregates results submitted by them.
 */
public class Master extends Listener implements Runnable {
    private static int GRID_CHUNK_MAX_LENGTH = Network.BUFFER_SIZE / 2;
    private static final Logger LOGGER = LoggerFactory.getLogger(Master.class);
    private final CommandMaster options;
    private Server server;
    private Simulator seed; // Stores the initial state of the grid
    private Set<Connection> approvedConnections = Sets.newHashSet();
    private int minIterations, maxIterations;

    public Master(CommandMaster options) {
        this.options = options;
    }

    @Override
    public void run() {
        server = new Server(Network.BUFFER_SIZE, Network.BUFFER_SIZE);

        Network.register(server);
        validateOptions(); // We use the servers Kryo instance
        server.addListener(this);

        try {
            server.bind(Optional.ofNullable(options.port).orElse(Network.SERVER_PORT_DEFAULT));
        } catch (IOException e) {
            LOGGER.error("Could not bind to the address: {}", e.getLocalizedMessage());
            System.exit(2);
        }

        server.start();
    }

    private void validateOptions() {
        try {
            Preconditions.checkNotNull(options.fileName, "The simulation file must be specified.");
            Preconditions.checkNotNull(options.survivalRange, "At least one rule set fitness condition must be specified.");

            try {
                this.minIterations = Integer.parseInt(options.survivalRange.get(0));
                this.maxIterations = Integer.parseInt(options.survivalRange.get(1));
            } catch (NumberFormatException e) {
                throw new Exception("Invalid survival range.");
            }

            Path path = Paths.get(options.fileName);

            Preconditions.checkArgument(Files.exists(path), "The simulation file was not found.");
            Preconditions.checkArgument(Files.isReadable(path), "The specified simulation file is not readable.");

            Input input = new Input(Files.newInputStream(path));

            try {
                seed = server.getKryo().readObject(input, Simulator.class);
            } finally {
                input.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            System.exit(2);
        }
    }

    @Override
    public void connected(Connection connection) {
        LOGGER.info("Receiving connection from {}...", connection.getRemoteAddressTCP());
    }

    private final Map<Connection, byte[]> resultMap = Maps.newIdentityHashMap();

    private void exportResult(byte[] rules) throws IOException {
        Kryo kryo = server.getKryo();
        LocalDateTime date = LocalDateTime.now();
        String fileName = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(date)
                .replace(':', '-') + ".rssim";
        Path path = Paths.get(options.outputDirectory, fileName).toAbsolutePath();
        Output output = new Output(Files.newOutputStream(path));
        Simulator simulator = seed.clone();
        RuleSet ruleSet = simulator.getRuleSet();

        ruleSet.setRules(rules);

        try {
            Files.createDirectories(path.getParent());
            kryo.writeObject(output, simulator);
            LOGGER.info("Written result to {}", path);
        } finally {
            output.close();
        }
    }

    private void handlePacket(Connection connection, Packet packet) throws IOException {
        if (packet instanceof MiningResultPacket) {
            MiningResultPacket mrp = (MiningResultPacket) packet;
            int ruleSetSize = seed.getRuleSet().getType().getRuleSetSize();
            boolean lastChunk = mrp.getRuleSetChunkOffset() + mrp.getRuleSetChunk().length >= ruleSetSize;
            byte[] rules = resultMap.computeIfAbsent(connection, key -> new byte[ruleSetSize]);

            System.arraycopy(mrp.getRuleSetChunk(), 0, rules, mrp.getRuleSetChunkOffset(), mrp.getRuleSetChunk().length);

            if (lastChunk) {
                resultMap.remove(connection);
                exportResult(rules);
            }
        }
    }

    private void requestMining(Connection connection) {
        connection.sendTCP(new MiningRequestPacket(seed, minIterations, maxIterations));

        Grid grid = seed.getGrid();
        int chunkOffset = 0;

        while (true) {
            byte[] chunk = grid.getTileStateChunk(chunkOffset, GRID_CHUNK_MAX_LENGTH);

            if (chunk.length <= 0) {
                break;
            }

            connection.sendTCP(new SeedGridChunkPacket(chunk, chunkOffset));

            chunkOffset += GRID_CHUNK_MAX_LENGTH;
        }
    }

    @Override
    public void received(Connection connection, Object packet) {
        if (!(packet instanceof Packet)) {
            return;
        }

        LOGGER.debug("Received packet from {}: {}", connection.getRemoteAddressTCP(), packet);

        if (approvedConnections.contains(connection)) {
            try {
                handlePacket(connection, (Packet) packet);
            } catch (IOException e) {
                LOGGER.error("An error occurred while trying to handle a packet", e);
            }
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
            } else {
                requestMining(connection);
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
