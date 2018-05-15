package cz.cvut.fel.hlusijak.network;

import com.esotericsoftware.kryonet.EndPoint;
import cz.cvut.fel.hlusijak.Master;
import cz.cvut.fel.hlusijak.Slave;
import cz.cvut.fel.hlusijak.util.SerializationUtil;

/**
 * Functions and values useful to both {@link Master} and {@link Slave}.
 */
public final class Network {
    public static final int SERVER_PORT_DEFAULT = 12993;
    public static final int CONNECTION_TIMEOUT_MILLIS = 5000;
    public static final int CONNECTION_RETRY_PERIOD = 5000;
    public static final int BUFFER_SIZE = 8192;

    private Network() {}

    public static void register(EndPoint endPoint) {
        SerializationUtil.register(endPoint.getKryo());
    }
}
