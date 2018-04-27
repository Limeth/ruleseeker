package cz.cvut.fel.hlusijak.util;

import java.nio.file.Path;
import java.util.Optional;

import javafx.scene.paint.Color;

import com.esotericsoftware.kryo.Kryo;

import org.objenesis.instantiator.ObjectInstantiator;

import cz.cvut.fel.hlusijak.network.ConnectionRequestPacket;
import cz.cvut.fel.hlusijak.network.ConnectionResultPacket;
import cz.cvut.fel.hlusijak.simulator.Simulator;

public final class SerializationUtil {
    public static void register(Kryo kryo) {
        // Persistence
        kryo.register(Simulator.class);
        kryo.register(Color.class, new ColorSerializer());

        // Network
        kryo.register(ConnectionRequestPacket.class);
        kryo.register(ConnectionResultPacket.class);
    }

    public static Kryo constructKryo() {
        Kryo kryo = new Kryo();

        register(kryo);

        return kryo;
    }

    public static Optional<String> getExtension(Path path) {
        String fileName = path.getFileName().toString();
        int i = fileName.lastIndexOf('.');

        if (i > 0) {
            return Optional.of(fileName.substring(i + 1));
        } else {
            return Optional.empty();
        }
    }
}
