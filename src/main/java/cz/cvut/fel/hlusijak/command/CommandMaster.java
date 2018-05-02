package cz.cvut.fel.hlusijak.command;

import cz.cvut.fel.hlusijak.command.PathConverter;

import java.nio.file.Path;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Run the miner master")
public class CommandMaster {
    @Parameter(names = {"--port", "-p"}, description = "Port to listen on for the inbound master connection")
    public Integer port;

    @Parameter(names = {"--output-directory", "-o"}, description = "Path to the directory to create and store the results in")
    public String outputDirectory;

    // There's a bug in JCommander where main arguments may not use custom type
    // converters.
    @Parameter(description = "The initial state of the grid")
    public String fileName;
}
