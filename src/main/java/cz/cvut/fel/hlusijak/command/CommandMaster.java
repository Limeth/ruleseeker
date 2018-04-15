package cz.cvut.fel.hlusijak.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Run the miner master")
public class CommandMaster {
    @Parameter(names = {"--port", "-p"}, description = "Port to listen on for the inbound master connection")
    public Integer port;
}
