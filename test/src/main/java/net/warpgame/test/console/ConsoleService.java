package net.warpgame.test.console;

import net.warpgame.engine.core.context.service.Service;
import org.apache.commons.lang3.ArrayUtils;

import java.io.PrintStream;
import java.util.ArrayList;

/**
 * @author KocproZ
 * Created 2018-01-09 at 22:06
 */
@Service
public class ConsoleService {

    private ArrayList<Command> commands = new ArrayList<>();
    private ArrayList<CommandVariable> variables = new ArrayList<>();
    private PrintStream output;

    public ConsoleService() {
        output = System.out;
        Command help = new Command("help", Command.Side.CLIENT, "Get help.");
        help.setExecutor((args) -> {
            if (args.length > 0)
                output.println(getHelpText(args[0]));
            else
                output.println("Use help [command]");
        });
        registerDefinition(help);

        Command list = new Command("list", Command.Side.CLIENT, "lists all commands");
        list.setExecutor((args) -> {
            output.println("Available commands:");
            for (Command c : commands) {
                output.printf("%5s\n", c.getCommand());
            }
        });
        registerDefinition(list);
    }

    /**
     * Adds Command to list of available commands.
     *
     * @param c Command to add
     */
    public void registerDefinition(Command c) {
        commands.add(c);
    }

    /**
     * Executes command
     *
     * @param line Line from console to parse
     */
    public void execute(String line) {
        String[] args = line.split(" ");
        String command = args[0];
        args = ArrayUtils.removeElement(args, command);

        for (Command definition : commands) {
            if (definition.getCommand().equals(command)) {
                if (definition.getSide() == Command.Side.CLIENT) {
                    boolean success = definition.execute(args);
                    if (!success) output.println(definition.getHelpText());
                } else {
                    //TODO send command to server
                }
                return;
            }
        }
        output.println("No such command. Type 'list' to list available commands");
    }

    /**
     * Returns help text associated with the command.
     *
     * @param command Command, eg. "list"
     * @return help text, null if command not found
     */
    public String getHelpText(String command) {
        for (Command c : commands)
            if (c.getCommand().equals(command))
                return c.getHelpText();
        return null;
    }

}