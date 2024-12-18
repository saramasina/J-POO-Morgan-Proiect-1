package org.poo.platform;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.*;
import org.poo.platform.commands.CommandExecutor;
import org.poo.platform.commands.CommandFactory;
import org.poo.platform.exchange.Exchange;
import org.poo.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class Bank {
    private final ArrayList<User> users;
    private final ArrayList<Exchange> exchangeRates;
    private ArrayList<Commerciant> commerciants;
    private ObjectInput input;
    private ArrayNode output;

    private static Bank instance;

    private Bank() {
        users = new ArrayList<>();
        exchangeRates = new ArrayList<>();
        commerciants = new ArrayList<>();
    }

    public static Bank getInstance() {
        if (instance == null) {
            instance = new Bank();
        }
        return instance;
    }

    /**
     * Sets up the bank's initial state by clearing existing data and adding
     * new users, exchange rates, and commerciants from the input
     *
     * @param input  The input data containing users, exchange rates, and commerciants
     * @param output The output data structure to store the results
     */
    public void setUpBank(final ObjectInput input, final ArrayNode output) {
        users.clear();
        exchangeRates.clear();
        commerciants.clear();
        this.input = input;
        this.output = output;
        for (UserInput user : input.getUsers()) {
            users.add(new User(user.getFirstName(), user.getLastName(), user.getEmail()));
        }
        for (ExchangeInput exchange : input.getExchangeRates()) {
            exchangeRates.add(new Exchange(exchange.getFrom(), exchange.getTimestamp(),
                    exchange.getRate(), exchange.getTo()));
        }
        if (input.getCommerciants() != null) {
            for (CommerciantInput commerciant : input.getCommerciants()) {
                commerciants.add(new Commerciant(commerciant.getCommerciants(),
                        commerciant.getDescription(), commerciant.getId()));
            }
        }

    }

    /**
     *
     * Method that starts the entire bank system and
     * maps the commands, executing them
     */
    public final void start() {
        CommandExecutor commandExecutor = new CommandExecutor();

        Stream<CommandInput> stream = Arrays.stream(input.getCommands());
        stream.map(commandInput -> {
                    CommandFactory.CommandType commandType = CommandFactory.CommandType.valueOf(commandInput.getCommand());
                    return CommandFactory.createCommand(commandType, commandInput, users, exchangeRates, output);
                })
                .forEach(command -> {
                    commandExecutor.setCommand(command);
                    commandExecutor.startExecution();
                });
        Utils.resetRandom();
    }
}
