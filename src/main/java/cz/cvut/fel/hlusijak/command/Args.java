package cz.cvut.fel.hlusijak.command;

import com.beust.jcommander.Parameter;

public class Args {
    public static final String SUBCOMMAND_MASTER = "master";
    public static final String SUBCOMMAND_SLAVE = "slave";

    @Parameter(names = {"--help", "-h"}, help = true)
    public boolean help;
}
