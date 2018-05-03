package cz.cvut.fel.hlusijak.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryonet.KryoSerialization;
import cz.cvut.fel.hlusijak.network.ConnectionRequestPacket;
import cz.cvut.fel.hlusijak.network.ConnectionResultPacket;
import cz.cvut.fel.hlusijak.network.MiningRequestPacket;
import cz.cvut.fel.hlusijak.network.MiningResultPacket;
import cz.cvut.fel.hlusijak.network.SeedGridChunkPacket;
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
    private static final int TYPE_INDEX_BASE_PACKET = 0;
    private static final int TYPE_INDEX_BASE_OTHER = 1 << 8;
    private static final int TYPE_INDEX_BASE_PRIMITIVE = 1 << 16;

    public static void register(Kryo kryo) {
        int typeIndex = TYPE_INDEX_BASE_PACKET;

        kryo.register(ConnectionRequestPacket.class, typeIndex++);
        kryo.register(ConnectionResultPacket.class, typeIndex++);
        kryo.register(MiningRequestPacket.class, typeIndex++);
        kryo.register(MiningResultPacket.class, typeIndex++);
        kryo.register(MiningResultPacket.class, typeIndex++);
        kryo.register(SeedGridChunkPacket.class, typeIndex++);

        typeIndex = TYPE_INDEX_BASE_OTHER;

        kryo.register(Simulator.class, typeIndex++);
        kryo.register(Grid.class, typeIndex++);
        kryo.register(SquareGridGeometry.class, typeIndex++);
        kryo.register(TriangleGridGeometry.class, typeIndex++);
        kryo.register(HexagonGridGeometry.class, typeIndex++);
        kryo.register(EdgeSumRuleSet.class, typeIndex++);
        kryo.register(VertexSumRuleSet.class, typeIndex++);
        kryo.register(CustomStateColoringMethod.class, typeIndex++);
        kryo.register(HueStateColoringMethod.class, typeIndex++);

        typeIndex = TYPE_INDEX_BASE_PRIMITIVE;

        kryo.register(byte[].class, typeIndex++);
        kryo.register(int[].class, typeIndex++);
        kryo.register(ArrayList.class, new CollectionSerializer(), typeIndex++);
        kryo.register(Color.class, new ColorSerializer(), typeIndex++);
        kryo.register(Vector2i.class, typeIndex++);
        kryo.register(Vector2d.class, typeIndex++);
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
