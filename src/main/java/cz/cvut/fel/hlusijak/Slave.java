package cz.cvut.fel.hlusijak;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.common.base.Preconditions;
import cz.cvut.fel.hlusijak.command.CommandSlave;
import cz.cvut.fel.hlusijak.network.ConnectionRequestPacket;
import cz.cvut.fel.hlusijak.network.Network;

import java.io.IOException;
import java.util.Optional;

public class Slave extends Listener implements Runnable {
    private CommandSlave options;

    public Slave(CommandSlave options) {
        this.options = options;
    }

    public void run() {
        validateOptions();

        Client client = new Client();

        Network.register(client);
        client.addListener(this);

        new Thread(() -> {
            try {
                client.connect(5000, options.masterAddress, Optional.ofNullable(options.masterPort).orElse(Network.SERVER_PORT_DEFAULT));
            } catch (IOException e) {
                System.err.printf("Could not connect to the server: %s\n", e.getLocalizedMessage());
                System.exit(2);
            }

            client.start();
        }).start();
    }

    private void validateOptions() {
        try {
            Preconditions.checkNotNull(options.masterAddress, "The master address must be specified.");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }

    @Override
    public void connected(Connection connection) {
        connection.sendTCP(new ConnectionRequestPacket(RuleSeeker.getInstance().getGitCommitId()));
    }

    @Override
    public void received(Connection connection, Object packet) {
        System.out.println(connection.getRemoteAddressTCP());
        System.out.printf("Received: ");
        System.out.println(packet);
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.printf("Disconnected: ");
        System.out.println(connection.getRemoteAddressTCP());
    }
}
