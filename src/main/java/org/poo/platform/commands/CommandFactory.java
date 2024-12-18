package org.poo.platform.commands;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.platform.User;
import org.poo.platform.commands.debug.commands.PrintTransactions;
import org.poo.platform.commands.debug.commands.PrintUsers;
import org.poo.platform.commands.workflow.commands.*;
import org.poo.platform.exchange.Exchange;

import java.util.ArrayList;

public class CommandFactory {
    public enum CommandType {
        printTransactions, printUsers, addAccount, addFunds, addInterest, changeInterestRate,
        checkCardStatus, createCard, createOneTimeCard, deleteAccount, deleteCard, payOnline,
        report, sendMoney, setAlias, setMinimumBalance, spendingsReport, splitPayment
    }

    public static Command createCommand(final CommandType commandType, final CommandInput commandInput,
                                        final ArrayList<User> users, final ArrayList<Exchange> exchangeRates,
                                        final ArrayNode output)  {
        return switch (commandType) {
            case addAccount -> new AddAccount(commandInput.getEmail(),
                    commandInput.getCurrency(), commandInput.getAccountType(),
                    commandInput.getTimestamp(), commandInput.getInterestRate(),
                    users);
            case deleteAccount -> new DeleteAccount(commandInput.getAccount(),
                    commandInput.getTimestamp(),
                    commandInput.getEmail(), users, output);
            case addFunds -> new AddFunds(commandInput.getAccount(),
                    commandInput.getAmount(), users);
            case createCard -> new CreateCard(commandInput.getEmail(), commandInput.getAccount(),
                    commandInput.getTimestamp(), users);
            case createOneTimeCard -> new CreateOneTimeCard(commandInput.getEmail(),
                    commandInput.getAccount(),
                    commandInput.getTimestamp(), users);
            case deleteCard -> new DeleteCard(commandInput.getCardNumber(),
                    commandInput.getTimestamp(), users);
            case payOnline -> new PayOnline(commandInput.getCardNumber(), commandInput.getAmount(),
                    commandInput.getCurrency(), commandInput.getTimestamp(),
                    commandInput.getCommerciant(), commandInput.getEmail(),
                    users, exchangeRates, output);
            case sendMoney -> new SendMoney(commandInput.getAccount(), commandInput.getReceiver(),
                    commandInput.getAmount(), commandInput.getTimestamp(),
                    commandInput.getDescription(), users, exchangeRates,
                    commandInput.getAlias());
            case setAlias -> new SetAlias(commandInput.getEmail(), commandInput.getAccount(),
                    commandInput.getAlias(), users);
            case printTransactions -> new PrintTransactions(commandInput.getEmail(),
                    commandInput.getTimestamp(), users, output);
            case checkCardStatus -> new CheckCardStatus(commandInput.getCardNumber(),
                    commandInput.getTimestamp(), users, output);
            case setMinimumBalance -> new SetMinimumBalance(commandInput.getAccount(),
                    commandInput.getAmount(), users);
            case splitPayment -> new SplitPayment(commandInput.getAccounts(),
                    commandInput.getAmount(), commandInput.getCurrency(),
                    commandInput.getTimestamp(), users, exchangeRates);
            case report -> new Report(commandInput.getAccount(),
                    commandInput.getStartTimestamp(), commandInput.getEndTimestamp(),
                    commandInput.getTimestamp(), users, output);
            case spendingsReport -> new SpendingsReport(commandInput.getAccount(),
                    commandInput.getStartTimestamp(), commandInput.getEndTimestamp(),
                    commandInput.getTimestamp(), users, output);
            case addInterest -> new AddInterest(commandInput.getAccount(),
                    commandInput.getTimestamp(), users, output);
            case changeInterestRate -> new ChangeInterestRate(commandInput.getInterestRate(),
                    commandInput.getAccount(), commandInput.getTimestamp(),
                    users, output);
            case printUsers -> new PrintUsers(users, commandInput.getTimestamp(), output);
        };
    }
}
