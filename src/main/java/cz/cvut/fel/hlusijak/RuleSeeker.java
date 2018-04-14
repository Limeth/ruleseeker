package cz.cvut.fel.hlusijak;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import cz.cvut.fel.hlusijak.command.Args;
import cz.cvut.fel.hlusijak.command.CommandMaster;
import cz.cvut.fel.hlusijak.command.CommandSlave;
import cz.cvut.fel.hlusijak.simulator.Simulator;
import cz.cvut.fel.hlusijak.simulator.SimulatorApplication;
import cz.cvut.fel.hlusijak.simulator.grid.Grid;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.GridGeometry;
import cz.cvut.fel.hlusijak.simulator.grid.geometry.TriangleGridGeometry;
import cz.cvut.fel.hlusijak.simulator.ruleset.RuleSet;
import cz.cvut.fel.hlusijak.simulator.ruleset.SumRuleSet;
import javafx.application.Application;

import java.util.Random;

public class RuleSeeker {
    private static RuleSeeker instance;
    private Simulator simulator;

    public RuleSeeker() {
        Random rng = new Random();
        GridGeometry gridGeometry = new TriangleGridGeometry(24, 12);
        RuleSet ruleSet = new SumRuleSet(gridGeometry, 4);

        ruleSet.randomizeRules(rng);

        Grid grid = new Grid(gridGeometry);

        grid.randomizeTileStates(rng, ruleSet);

        this.simulator = new Simulator(grid, ruleSet, 10);
    }

    public Simulator getSimulator() {
        return simulator;
    }

    public static void main(String[] rawArgs) {
        instance = new RuleSeeker();
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
                    System.out.println("I'm a master!");
                    return;
                case Args.SUBCOMMAND_SLAVE:
                    System.out.println("I'm a slave!");
                    return;
            }
        }

        Application.launch(SimulatorApplication.class, rawArgs);
    }

    public static RuleSeeker getInstance() {
        return instance;
    }
}
