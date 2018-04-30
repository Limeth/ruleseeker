package cz.cvut.fel.hlusijak.command;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.beust.jcommander.IStringConverter;

public class PathConverter implements IStringConverter<Path> {
    @Override
    public Path convert(String value) {
        return Paths.get(value);
    }
}
