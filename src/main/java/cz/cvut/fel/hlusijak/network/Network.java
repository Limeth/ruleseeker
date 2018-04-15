package cz.cvut.fel.hlusijak.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public final class Network {
    public static final int SERVER_PORT_DEFAULT = 12993;

    private Network() {}

    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

        kryo.register(ConnectionRequestPacket.class);
        kryo.register(ConnectionResultPacket.class);
    }
}
