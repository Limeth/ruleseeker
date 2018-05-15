package cz.cvut.fel.hlusijak;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import cz.cvut.fel.hlusijak.command.Args;
import cz.cvut.fel.hlusijak.command.CommandMaster;
import cz.cvut.fel.hlusijak.command.CommandSlave;
import cz.cvut.fel.hlusijak.simulator.Simulator;
import cz.cvut.fel.hlusijak.simulator.SimulatorApplication;
import javafx.application.Application;

import java.io.IOException;
import java.util.Properties;

/**
 * The entry point of the application. Here, the arguments are handled and it is
 * determined whether to run the simulation interface or the CA space miner.
 */
public class RuleSeeker implements Runnable {
    private static RuleSeeker instance;
    private String[] rawArgs;
    private String projectArtifactId;
    private String projectVersion;
    private String gitCommitId;
    private Simulator simulator;

    private RuleSeeker(String[] rawArgs) {
        // Log.TRACE();
        instance = this;
        this.rawArgs = rawArgs;

        Properties properties = new Properties();

        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("build.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.projectArtifactId = (String) properties.get("project.artifactId");
        this.projectVersion = (String) properties.get("project.version");
        this.gitCommitId = (String) properties.get("git.commit.id");

        System.out.printf("-----------------------------------------------------------\n");
        System.out.printf("%s\n", this.projectArtifactId);
        System.out.printf("\tVersion: %s\n", this.projectVersion);
        System.out.printf("\tCommit ID: %s\n", this.gitCommitId);
        System.out.printf("-----------------------------------------------------------\n");
    }

    /**
     * @param simulator The new {@link Simulator} instance.
     * @return The previous {@link Simulator} instance.
     */
    public Simulator setSimulator(Simulator simulator) {
        Simulator prev = this.simulator;
        this.simulator = simulator;

        return prev;
    }

    /**
     * @return The global {@link Simulator} instance, may be {@code null}.
     */
    public Simulator getSimulator() {
        return simulator;
    }

    /**
     * If this method has been called, your Java Virtual Machine™ is probably working.
     */
    public static void main(String[] rawArgs) {
        new RuleSeeker(rawArgs).run();
    }

    @Override
    public void run() {
        Args args = new Args();
        CommandMaster cmdMaster = new CommandMaster();
        CommandSlave cmdSlave = new CommandSlave();

        JCommander jc = JCommander.newBuilder()
            .addObject(args)
            .addCommand(Args.SUBCOMMAND_MASTER, cmdMaster)
            .addCommand(Args.SUBCOMMAND_SLAVE, cmdSlave)
            .build();

        try {
            jc.parse(rawArgs);
        } catch (ParameterException e) {
            System.err.printf("Invalid arguments: %s\n", e.getLocalizedMessage());
            System.exit(1);
        }

        if (args.help) {
            jc.usage();
            return;
        }

        if (jc.getParsedCommand() != null) {
            switch (jc.getParsedCommand()) {
                case Args.SUBCOMMAND_MASTER:
                    new Master(cmdMaster).run();
                    return;
                case Args.SUBCOMMAND_SLAVE:
                    new Slave(cmdSlave).run();
                    return;
            }
        }

        Application.launch(SimulatorApplication.class, rawArgs);
    }

    /**
     * @return The one and only {@link RuleSeeker}. (Unless you've abused the reflection API)
     */
    public static RuleSeeker getInstance() {
        return instance;
    }

    /**
     * @return The project's artifact id. (determined during compilation)
     */
    public String getProjectArtifactId() {
        return this.projectArtifactId;
    }

    /**
     * @return The project version. (determined during compilation)
     */
    public String getProjectVersion() {
        return this.projectVersion;
    }

    /**
     * @return The commit id. (determined during compilation)
     */
    public String getGitCommitId() {
        return this.gitCommitId;
    }
}
