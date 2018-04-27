package cz.cvut.fel.hlusijak.util;

import javafx.scene.paint.Color;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ColorSerializer extends Serializer<Color> {
    public Color read(Kryo kryo, Input input, Class<Color> type) {
        return new Color(input.readDouble(), input.readDouble(), input.readDouble(), input.readDouble());
    }

    public void write(Kryo kryo, Output output, Color object) {
        output.writeDouble(object.getRed());
        output.writeDouble(object.getGreen());
        output.writeDouble(object.getBlue());
        output.writeDouble(object.getOpacity());
    }
}
