package cz.cvut.fel.hlusijak.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

@Parameters(commandDescription = "Run the miner master")
public class CommandMaster {
    @Parameter(names = {"--port", "-p"}, description = "Port to listen on for the inbound master connection")
    public Integer port;

    @Parameter(names = {"--output-directory", "-o"}, description = "Path to the directory to create and store the results in")
    public String outputDirectory;

    @Parameter(names = {"--survival-range", "-s"}, arity = 2, description = "Rule set fitness condition, where the simulation must \"die out\" within the specified range of iterations")
    public List<String> survivalRange;

    // There's a bug in JCommander where main arguments may not use custom type
    // converters.
    @Parameter(description = "The initial state of the grid")
    public String fileName;
}
