package cz.cvut.fel.hlusijak.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Run the miner slave")
public class CommandSlave {
    @Parameter(description = "The address of the master server")
    public String masterAddress;

    @Parameter(names = {"--port", "-p"}, description = "The port of the master server")
    public Integer masterPort;
}
