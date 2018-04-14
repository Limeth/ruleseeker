package cz.cvut.fel.hlusijak.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Run the miner slave")
public class CommandSlave {
    @Parameter(names = {"--port", "-p"}, description = "Port to listen on for the inbound master connection")
    public int port;
}
