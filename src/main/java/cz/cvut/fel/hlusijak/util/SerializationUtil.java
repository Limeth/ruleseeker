package cz.cvut.fel.hlusijak.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryonet.KryoSerialization;
import cz.cvut.fel.hlusijak.network.ConnectionRequestPacket;
import cz.cvut.fel.hlusijak.network.ConnectionResultPacket;
import cz.cvut.fel.hlusijak.network.MiningRequestPacket;
import cz.cvut.fel.hlusijak.network.MiningResultPacket;
import cz.cvut.fel.hlusijak.simulator.Simulator;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.HexagonGridGeometry;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.SquareGridGeometry;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.TriangleGridGeometry;
import cz.cvut.fel.hlusijak.simulator.ruleset.EdgeSumRuleSet;
import cz.cvut.fel.hlusijak.simulator.ruleset.VertexSumRuleSet;
import cz.cvut.fel.hlusijak.simulator.stateColoringMethod.CustomStateColoringMethod;
import cz.cvut.fel.hlusijak.simulator.stateColoringMethod.HueStateColoringMethod;
import javafx.scene.paint.Color;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

public final class SerializationUtil {
    public static void register(Kryo kryo) {
        kryo.register(ConnectionRequestPacket.class);
        kryo.register(ConnectionResultPacket.class);
        kryo.register(MiningRequestPacket.class);
        kryo.register(MiningResultPacket.class);

        kryo.register(Simulator.class);
        kryo.register(Grid.class);
        kryo.register(SquareGridGeometry.class);
        kryo.register(TriangleGridGeometry.class);
        kryo.register(HexagonGridGeometry.class);
        kryo.register(EdgeSumRuleSet.class);
        kryo.register(VertexSumRuleSet.class);
        kryo.register(CustomStateColoringMethod.class);
        kryo.register(HueStateColoringMethod.class);

        kryo.register(byte[].class);
        kryo.register(int[].class);
        kryo.register(ArrayList.class, new CollectionSerializer());
        kryo.register(Color.class, new ColorSerializer());
        kryo.register(Vector2i.class);
        kryo.register(Vector2d.class);
    }

    public static Kryo constructKryo() {
        // Use Kryo defaults from Kryonet
        Kryo kryo = new KryoSerialization().getKryo();

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
