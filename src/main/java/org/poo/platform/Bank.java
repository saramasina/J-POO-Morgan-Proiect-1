package org.poo.platform;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.*;
import org.poo.platform.commands.Command;
import org.poo.platform.commands.CommandExecutor;
import org.poo.platform.commands.debug.commands.PrintTransactions;
import org.poo.platform.commands.debug.commands.PrintUsers;
import org.poo.platform.commands.workflow.commands.*;
import org.poo.platform.exchange.Exchange;
import org.poo.utils.Utils;

import java.util.ArrayList;

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
     * Sets up the bank's initial state by clearing existing data and populating it
     * with new users, exchange rates, and commerciants from the input
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
     * iterates through the commands, executing them
     */
    public final void start() {
        CommandExecutor commandExecutor = new CommandExecutor();

        for (CommandInput commandInput : input.getCommands()) {
            Command command = null;
            switch (commandInput.getCommand()) {
                case "addAccount" : {
                    command = new AddAccount(commandInput.getEmail(),
                            commandInput.getCurrency(), commandInput.getAccountType(),
                            commandInput.getTimestamp(), commandInput.getInterestRate(),
                            users);
                    break;
                }
                case "deleteAccount" : {
                    command = new DeleteAccount(commandInput.getAccount(),
                            commandInput.getTimestamp(),
                            commandInput.getEmail(), users, output);
                    break;
                }
                case "printUsers" : {
                    command = new PrintUsers(users, commandInput.getTimestamp(), output);
                    break;
                }
                case "addFunds" : {
                    command = new AddFunds(commandInput.getAccount(),
                            commandInput.getAmount(), users);
                    break;
                }
                case "createCard" : {
                    command = new CreateCard(commandInput.getEmail(), commandInput.getAccount(),
                            commandInput.getTimestamp(), users);
                    break;
                }
                case "createOneTimeCard" : {
                    command = new CreateOneTimeCard(commandInput.getEmail(),
                            commandInput.getAccount(),
                            commandInput.getTimestamp(), users);
                    break;
                }
                case "deleteCard" : {
                    command = new DeleteCard(commandInput.getCardNumber(),
                            commandInput.getTimestamp(), users);
                    break;
                }
                case "payOnline" : {
                    command = new PayOnline(commandInput.getCardNumber(), commandInput.getAmount(),
                            commandInput.getCurrency(), commandInput.getTimestamp(),
                            commandInput.getCommerciant(), commandInput.getEmail(),
                            users, exchangeRates, output);
                    break;
                }
                case "sendMoney" : {
                    command = new SendMoney(commandInput.getAccount(), commandInput.getReceiver(),
                            commandInput.getAmount(), commandInput.getTimestamp(),
                            commandInput.getDescription(), users, exchangeRates,
                            commandInput.getAlias());
                    break;
                }
                case "setAlias" : {
                    command = new SetAlias(commandInput.getEmail(), commandInput.getAccount(),
                            commandInput.getAlias(), users);
                    break;
                }
                case "printTransactions" : {
                    command = new PrintTransactions(commandInput.getEmail(),
                            commandInput.getTimestamp(), users, output);
                    break;
                }
                case "checkCardStatus" : {
                    command = new CheckCardStatus(commandInput.getCardNumber(),
                            commandInput.getTimestamp(), users, output);
                    break;
                }
                case "setMinimumBalance" : {
                    command = new SetMinimumBalance(commandInput.getAccount(),
                            commandInput.getAmount(), users);
                    break;
                }
                case "splitPayment" : {
                    command = new SplitPayment(commandInput.getAccounts(),
                            commandInput.getAmount(), commandInput.getCurrency(),
                            commandInput.getTimestamp(), users, exchangeRates);
                    break;
                }
                case "report" : {
                    command = new Report(commandInput.getAccount(),
                            commandInput.getStartTimestamp(), commandInput.getEndTimestamp(),
                            commandInput.getTimestamp(), users, output);
                    break;
                }
                case "spendingsReport" : {
                    command = new SpendingsReport(commandInput.getAccount(),
                            commandInput.getStartTimestamp(), commandInput.getEndTimestamp(),
                            commandInput.getTimestamp(), users, output);
                    break;
                }
                case "addInterest" : {
                    command = new AddInterest(commandInput.getAccount(),
                            commandInput.getTimestamp(), users, output);
                    break;
                }
                case "changeInterestRate" : {
                    command = new ChangeInterestRate(commandInput.getInterestRate(),
                            commandInput.getAccount(), commandInput.getTimestamp(),
                            users, output);
                    break;
                }
                default : {
                    break;
                }
            }
            commandExecutor.setCommand(command);
            commandExecutor.startExecution();
        }
        Utils.resetRandom();
    }
}
