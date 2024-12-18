package org.poo.platform.commands.workflow.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.poo.platform.accounts.Account;
import org.poo.platform.exchange.CurrencyConverter;
import org.poo.platform.exchange.Exchange;
import org.poo.platform.User;
import org.poo.platform.commands.Command;

import java.util.ArrayList;
import java.util.List;

public final class SplitPayment implements Command {
    private ArrayList<Account> accounts = new ArrayList<>();
    private ArrayList<User> users = new ArrayList<>();
    private List<String> accountsIban;
    private double amount;
    private double totalAmount;
    private int timestamp;
    private CurrencyConverter converter = new CurrencyConverter();
    private String currency;

    public SplitPayment(final List<String> accounts, final double amount,
                        final String currency, final int timestamp,
                        final ArrayList<User> users, final ArrayList<Exchange> exchangeRates) {
        this.timestamp = timestamp;
        this.amount = amount / accounts.size();
        totalAmount = amount;
        this.currency = currency;
        accountsIban = accounts;
        for (String account : accounts) {
            for (User user : users) {
                for (Account accountUser : user.getAccounts()) {
                    // create ArrayLists with the users and their accounts
                    if (account.equals(accountUser.getIban())) {
                        this.accounts.add(accountUser);
                        this.users.add(user);
                    }
                }
            }
        }
        for (Exchange exchange : exchangeRates) {
            converter.addExchangeRate(exchange.getFrom(), exchange.getTo(), exchange.getRate());
        }
    }

    /**
     * Processes a split payment operation
     * by verifying account balances and performing the transaction.
     * If any account has insufficient funds, appropriate error details are logged.
     */
    @Override
    public void operation() {
        String iban = null;
        for (Account accountUser : accounts) {
            double amountToPay;
            if (!accountUser.getCurrency().equals(currency)) {
                amountToPay = converter.convert(currency, accountUser.getCurrency(), amount);
            } else {
                amountToPay = amount;
            }
            // we need the last account listed that has insufficient funds for the transaction
            if (accountUser.getBalance() - amountToPay < accountUser.getMinBalance()) {
                iban = accountUser.getIban();
            }
        }

        // one of the accounts has insufficient funds
        if (iban != null) {
            for (Account account : accounts) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode outputNode = mapper.createObjectNode();
                outputNode.put("description",
                        String.format("Split payment of %.2f %s", totalAmount, currency));
                outputNode.put("timestamp", timestamp);
                outputNode.put("currency", currency);
                outputNode.put("amount", amount);
                outputNode.put("error", "Account " + iban
                        + " has insufficient funds for a split payment.");
                outputNode.putPOJO("involvedAccounts", accountsIban);

                account.getTransactions().add(outputNode);
            }
            return;
        }
        setBalances();
    }

    private void setBalances() {
        for (Account accountUser : accounts) {
            double amountToPay;
            if (!accountUser.getCurrency().equals(currency)) {
                amountToPay = converter.convert(currency, accountUser.getCurrency(), amount);
            } else {
                amountToPay = amount;
            }

            accountUser.setBalance(accountUser.getBalance() - amountToPay);

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode outputNode = mapper.createObjectNode();
            outputNode.put("description",
                    String.format("Split payment of %.2f %s", totalAmount, currency));
            outputNode.put("timestamp", timestamp);
            outputNode.put("currency", currency);
            outputNode.put("amount", amount);
            outputNode.putPOJO("involvedAccounts", accountsIban);

            accountUser.getTransactions().add(outputNode);
        }
    }
}
