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

public class RuleSeeker implements Runnable {
    public static boolean DEBUG = true;
    private static RuleSeeker instance;
    private String[] rawArgs;
    private String projectArtifactId;
    private String projectVersion;
    private String gitCommitId;
    private Simulator simulator;

    private RuleSeeker(String[] rawArgs) {
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

    public Simulator setSimulator(Simulator simulator) {
        Simulator prev = this.simulator;
        this.simulator = simulator;

        return prev;
    }

    public Simulator getSimulator() {
        return simulator;
    }

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

    public static RuleSeeker getInstance() {
        return instance;
    }

    public String getProjectArtifactId() {
        return this.projectArtifactId;
    }

    public String getProjectVersion() {
        return this.projectVersion;
    }

    public String getGitCommitId() {
        return this.gitCommitId;
    }
}
