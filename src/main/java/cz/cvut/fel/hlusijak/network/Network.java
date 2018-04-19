package cz.cvut.fel.hlusijak.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public final class Network {
    public static final int SERVER_PORT_DEFAULT = 12993;
    public static final int CONNECTION_TIMEOUT_MILLIS = 5000;
    public static final int IDLE_THRESHOLD_MILLIS = 30000;
    public static final int CONNECTION_RETRY_PERIOD = 5000;

    private Network() {}

    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        kryo.register(ConnectionRequestPacket.class);
        kryo.register(ConnectionResultPacket.class);
    }
}
